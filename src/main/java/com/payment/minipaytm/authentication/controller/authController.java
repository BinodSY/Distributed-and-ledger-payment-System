package com.payment.minipaytm.authentication.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class authController {

    @PostMapping("/users")
    public String users(){
        return "ok";
    }

    @PostMapping("/login")
    public String login(){
        return "ok";
    }
}
