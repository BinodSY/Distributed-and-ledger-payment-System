package com.payment.minipaytm.transaction;

import java.util.UUID;

public record TransferResult(
        UUID txnId,
        String status,
        Long fromWalletBalance,
        Long toWalletBalance
) {

    public static TransferResult success(
            UUID txnId,
            long fromBalance,
            long toBalance
    ) {
        return new TransferResult(txnId, "SUCCESS", fromBalance, toBalance);
    }

    public static TransferResult from(Transaction txn) {
        return new TransferResult(txn.getTxnId(), txn.getStatus().name(), null, null);
    }
}
