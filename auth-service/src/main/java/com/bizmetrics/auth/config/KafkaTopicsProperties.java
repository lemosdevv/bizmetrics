package com.bizmetrics.auth.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.kafka")
@Getter
@Setter
public class KafkaTopicsProperties {

    private boolean autoCreateTopics = true;

    private String dltSuffix = ".DLT";

    private Integer concurrency = 3;

    
    // Map de tópicos gerenciados por este serviço
    // exemplo no application.yml:
    // app.kafka.topics:
    //   kpi.events.ingest: { partitions: 3, replicationFactor: 1 }
    //   kpi.events.alerts: { partitions: 3, replicationFactor: 1 }

    private Map<String, Topic> topics = new LinkedHashMap<>();

        public static class Topic {
            private int partitions = 3;
            private short replicationFactor = 1;

            public int getPartitions() {
                return partitions;
            }

            public void setPartitions(int partitions) {
                this.partitions = partitions;
            }

            public short getReplicationFactor() {
                return replicationFactor;
            }

            public void setReplicationFactor(short replicationFactor) {
                this.replicationFactor = replicationFactor;
            }
        }

}
