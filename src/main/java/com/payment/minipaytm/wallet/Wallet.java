package com.payment.minipaytm.wallet;


import java.time.OffsetDateTime;
import java.util.UUID;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import jakarta.persistence.Id;

import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;


@Getter
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue
    private UUID walletId;

    @Column(nullable = false, updatable = false,unique = true)
    private UUID userId;

    @Column(nullable = false)
    private long balanceCache;

    @Version
    private long version;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Wallet() {}

    public Wallet(UUID userId) {
        this.userId = userId;
        this.balanceCache = 0L;
        this.createdAt = OffsetDateTime.now();
    }

    // ❗ Controlled mutations only
    public void debit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException();
        if (balanceCache < amount) throw new IllegalStateException("Insufficient balance");
        balanceCache -= amount;
    }

    public void credit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException();
        balanceCache += amount;
    }
}
