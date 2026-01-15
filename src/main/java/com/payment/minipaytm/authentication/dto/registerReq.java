package com.payment.minipaytm.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class registerReq {
private String email;
private String name;
private String password;
private String phone;
}
