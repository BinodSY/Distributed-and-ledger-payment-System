package com.payment.minipaytm.authentication.dto;

import java.util.UUID;

public record CustomUserPrincipal(
        UUID userId,
        String email,
        String role
) {}
