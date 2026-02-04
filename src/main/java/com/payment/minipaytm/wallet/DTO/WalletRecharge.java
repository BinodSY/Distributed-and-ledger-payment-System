package com.payment.minipaytm.wallet.DTO;

import java.util.UUID;

public record WalletRecharge(
    UUID walletId,
    Long amount
) {

}
