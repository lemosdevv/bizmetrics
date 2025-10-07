package com.bizmetrics.auth.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bizmetrics.auth.dtos.UserDto;
import com.bizmetrics.auth.excecoes.BusinessExcepetion;
import com.bizmetrics.auth.excecoes.ResourceNotFoundException;
import com.bizmetrics.auth.models.Company;
import com.bizmetrics.auth.models.User;
import com.bizmetrics.auth.repositories.CompanyRepository;
import com.bizmetrics.auth.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserDto user){
        if(userRepository.existsByUsername(user.username()) || userRepository.existsByEmail(user.email())){
            throw new BusinessExcepetion("Username or email already exists");
        }

        Company company = companyRepository.findById(user.companyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        User newUser = new User();
        newUser.setUsername(user.username());
        newUser.setEmail(user.email());
        newUser.setPassword(passwordEncoder.encode(user.password()));
        newUser.setCompany(company);

        return userRepository.save(newUser);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void deleteUser(Long id){
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

}
