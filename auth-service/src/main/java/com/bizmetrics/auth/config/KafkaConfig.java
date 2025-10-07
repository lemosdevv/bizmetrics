package com.bizmetrics.auth.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableKafka
@Configuration
public class KafkaConfig {

    // Producer
    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties properties) {
        Map<String, Object> configs = new HashMap<>(properties.buildProducerProperties());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // JSON seguro e estável
        configs.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        configs.put(JsonSerializer.TYPE_MAPPINGS, "");
        // transações (opcional, mas robusto)
        configs.putIfAbsent(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "kpi-tx-" + System.currentTimeMillis());
        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(configs);
        factory.setTransactionIdPrefix("kpi-tx-");
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(pf);
        template.setObservationEnabled(true); // micrometer
        return template;
    }

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(ProducerFactory<String, Object> pf) {
        return new KafkaTransactionManager<>(pf);
    }

    // Consumer
    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties properties) {
        Map<String, Object> configs = new HashMap<>(properties.buildConsumerProperties());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configs.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Object.class.getName());
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> template,
                                                 KafkaTopicsProperties appKafkaProps) {
        var backoff = new ExponentialBackOff();
        backoff.setInitialInterval(1_000L);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(30_000L);
        backoff.setMaxElapsedTime(Long.MAX_VALUE); // Optional: disables max elapsed time

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            template,
            (org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> r, Exception e) -> {
                String originalTopic = r.topic();
                return new org.apache.kafka.common.TopicPartition(
                        originalTopic + appKafkaProps.getDltSuffix(), r.partition());
            });

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backoff);
        // Exceções que não valem retry
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> cf,
            DefaultErrorHandler errorHandler,
            KafkaTopicsProperties appKafkaProps) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(cf);
        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(appKafkaProps.getConcurrency());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.getContainerProperties().setObservationEnabled(true);
        return factory;
    }

    // Admin + criação de tópicos (opcional/ativável)
    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProperties properties) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", properties.getBootstrapServers()));
        return new KafkaAdmin(configs);
    }

    @Bean
    public List<NewTopic> topics(KafkaTopicsProperties props) {
        if (!props.isAutoCreateTopics()) return List.of();
        return props.getTopics().entrySet().stream().flatMap(e -> {
            String name = e.getKey();
            var t = e.getValue();

            var main = TopicBuilder.name(name)
                    .partitions(t.getPartitions())
                    .replicas(t.getReplicationFactor())
                    .build();

            var dlt = TopicBuilder.name(name + props.getDltSuffix())
                    .partitions(t.getPartitions())
                    .replicas(t.getReplicationFactor())
                    .build();

            return List.of(main, dlt).stream();
        }).collect(Collectors.toList());
    }
}
