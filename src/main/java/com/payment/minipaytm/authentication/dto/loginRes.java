package com.payment.minipaytm.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class loginRes {
    private String token;
    private String email;
}
