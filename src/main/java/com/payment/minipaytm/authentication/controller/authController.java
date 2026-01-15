package com.payment.minipaytm.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.minipaytm.authentication.dto.loginRequest;
import com.payment.minipaytm.authentication.dto.loginRes;
import com.payment.minipaytm.authentication.service.authService;


@RestController
@RequestMapping("/auth")
public class authController {

    @Autowired
    private authService authservice;
    
    @GetMapping("/health-check")
    public String healthCheck(){
        return "application is running fine";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginRequest request){
        String email=request.getEmail();
        String password=request.getPassword();
        loginRes loginres=authservice.login(email,password);
        return ResponseEntity.ok(loginres);
    }
}
