package com.payment.minipaytm.authentication.controller;
import com.payment.minipaytm.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.minipaytm.authentication.dto.regRes;
import com.payment.minipaytm.authentication.dto.registerReq;


@RestController
@RequestMapping("/")
public class publicController {

   
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public regRes register(@RequestBody registerReq request){
        return userService.registerUser(request);
    }
}
