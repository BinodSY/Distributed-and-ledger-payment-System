package com.payment.minipaytm.user.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.minipaytm.authentication.dto.AuthUserDTO;
import com.payment.minipaytm.authentication.interfaces.UserAuthPort;
import com.payment.minipaytm.user.model.User;
import com.payment.minipaytm.user.reposistory.UserRepository;
 

@Service
public class UserAuthAdapter implements UserAuthPort{

     @Autowired
    private UserRepository userRepository;

    public AuthUserDTO findByEmail(String email) {

        User user=userRepository.findByEmail(email);
        if (user == null) {
                throw new IllegalArgumentException("unauthorized person");
            }
        
        return new AuthUserDTO(
            user.getEmail(),
            user.getPassword()
        );
         
            
    }

}
