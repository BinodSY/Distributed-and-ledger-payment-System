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

    @Override
    public AuthUserDTO findByEmail(String email) {

        User user=userRepository.findByEmail(email);
        if (user == null) {
                throw new IllegalArgumentException("user not found");
            }
        
        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setUserId(user.getUserId());
        authUserDTO.setEmail(user.getEmail());
        authUserDTO.setPasswordHash(user.getPasswordHash());
        return authUserDTO;
    }

}
