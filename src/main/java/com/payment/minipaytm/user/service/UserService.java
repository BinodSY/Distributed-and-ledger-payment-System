package com.payment.minipaytm.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.payment.minipaytm.user.reposistory.UserRepository;
import com.payment.minipaytm.authentication.dto.regRes;
import com.payment.minipaytm.authentication.dto.registerReq;
import com.payment.minipaytm.user.dto.UserResponse;
import com.payment.minipaytm.user.model.User;

@Service
public class UserService {

    

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserResponse UserDetails(String email){
        User user = userRepository.findByEmail(email);

        return new UserResponse(user);
        
    }

    public regRes registerUser(registerReq regReq){
        User user = new User();
        user.setName(regReq.getName());
        user.setEmail(regReq.getEmail());
        user.setPasswordHash(passwordEncoder.encode(regReq.getPassword()));
        user.setPhone(regReq.getPhone());
        // Set other properties from regReq as needed
        User savedUser = userRepository.save(user);
        regRes response = new regRes();
        response.setMessage("User registered successfully");
        response.setName(savedUser.getName());
        response.setUserId(savedUser.getUserId());
        return response;
    }
}
