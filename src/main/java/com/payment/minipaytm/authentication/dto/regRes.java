package com.payment.minipaytm.authentication.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class regRes {
    private UUID userId;
    private String message;
    private String name;
}
