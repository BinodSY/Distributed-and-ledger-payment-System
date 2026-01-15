package com.payment.minipaytm.authentication.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.payment.minipaytm.authentication.dto.AuthUserDTO;
import com.payment.minipaytm.user.service.UserAuthAdapter;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserAuthAdapter userAuthAdapt;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        AuthUserDTO authRes=userAuthAdapt.findByEmail(email);
        if (authRes == null) {
    throw new RuntimeException("User not found");
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(authRes.getEmail())
                .password(authRes.getPasswordHash())
                .roles("USER") // You can set roles as needed
                .build();
    }


}
