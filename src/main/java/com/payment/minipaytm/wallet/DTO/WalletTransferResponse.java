package com.payment.minipaytm.wallet.DTO;

import java.util.UUID;

public record WalletTransferResponse(
        UUID txnId,
        String status,
        Long fromWalletBalance,
        Long toWalletBalance
) {}
