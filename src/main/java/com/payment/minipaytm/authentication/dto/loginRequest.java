package com.payment.minipaytm.authentication.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class loginRequest {
     private String email;
     private String password;
}
