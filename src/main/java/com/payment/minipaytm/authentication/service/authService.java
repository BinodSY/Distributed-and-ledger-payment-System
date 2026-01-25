package com.payment.minipaytm.authentication.service;

import java.time.Duration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import com.payment.minipaytm.authentication.configs.JwtUtil;
import com.payment.minipaytm.authentication.dto.loginRes;
import com.payment.minipaytm.user.model.User;
import com.payment.minipaytm.user.reposistory.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;



@Service
public class authService {

    @Autowired
    private JwtUtil  jwtUtil;
    
    @Autowired
    private  AuthenticationManager authenticationManager;

    


    @Autowired
    private UserRepository userrepo;
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    public loginRes login(String email,String password){

        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

        User user=userrepo.findByEmail(email);
        String emailId=authentication.getName();

        String token= jwtUtil.generateToken(emailId,user.getUserId());
        String refreshToken = refreshTokenService.createAndStore(user.getUserId());

        // refresh cookie maxAge should match refresh token lifetime (example 7 days)
        long maxAgeSeconds = Duration.ofDays(7).toSeconds();

        loginRes loginres=new loginRes();
            loginres.setToken(token);
            loginres.setRefreshToken(refreshToken);
            loginres.setEmail(emailId);
            loginres.setExpiresInSeconds(maxAgeSeconds);

        return loginres;
            
    }

    
}
