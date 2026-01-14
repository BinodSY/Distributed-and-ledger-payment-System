package com.payment.minipaytm.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.payment.minipaytm.user.reposistory.UserRepository;
import com.payment.minipaytm.user.dto.UserResponse;
import com.payment.minipaytm.user.model.User;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public UserResponse UserDetails(String email){
        User user = userRepository.findByEmail(email);
        return new UserResponse(
            user.getUserId(),
            user.getEmail(),
            user.getPassword()
        );
    }
}
