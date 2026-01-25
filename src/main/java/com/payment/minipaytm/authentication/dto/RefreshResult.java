package com.payment.minipaytm.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshResult {
    private String accessToken;
    private String newRefreshToken;
    private String email;
    private long expiresInSeconds;
}