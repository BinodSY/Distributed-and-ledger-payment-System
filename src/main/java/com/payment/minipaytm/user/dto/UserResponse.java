package com.payment.minipaytm.user.dto;
import java.util.UUID;
public record UserResponse(
    UUID userId,
    String email,
    String password
) {}
