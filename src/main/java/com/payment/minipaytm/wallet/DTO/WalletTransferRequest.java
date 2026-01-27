package com.payment.minipaytm.wallet.DTO;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record WalletTransferRequest(
        
        @NotNull
        UUID fromWalletId,

        @NotNull
        UUID toWalletId,

        @Positive
        long amount,

        @NotNull
        String idempotencyKey
) {}
