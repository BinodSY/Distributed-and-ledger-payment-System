package com.payment.minipaytm.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import com.payment.minipaytm.authentication.config.JwtUtil;
import com.payment.minipaytm.authentication.dto.loginRes;

import org.springframework.security.authentication.AuthenticationManager;



@Service
public class authService {

    @Autowired
    private JwtUtil  jwtUtil;
    
    @Autowired
    private  AuthenticationManager authenticationManager;
    
    public  loginRes login(String email,String password){

        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            String emailAdd=authentication.getName();

        String token= jwtUtil.generateToken(emailAdd);
            loginRes loginres=new loginRes();
            loginres.setToken(token);
            loginres.setEmail(emailAdd);

            return loginres;
            
    }
}
