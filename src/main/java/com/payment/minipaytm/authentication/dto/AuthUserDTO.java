package com.payment.minipaytm.authentication.dto;



public record AuthUserDTO(
    String email,
    String passwordHash
) {}
