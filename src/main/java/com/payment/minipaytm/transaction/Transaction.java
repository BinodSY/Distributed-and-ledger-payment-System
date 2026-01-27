package com.payment.minipaytm.transaction;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "transactions",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_txn_idempotency",
            columnNames = {"idempotency_key"}
        )
    },
    indexes = {
        @Index(name = "idx_txn_created_at", columnList = "created_at"),
        @Index(name = "idx_txn_source_ref", columnList = "source_ref"),
        @Index(name = "idx_txn_destination_ref", columnList = "destination_ref")
    }
)
public class Transaction {

    @Id
    @GeneratedValue
    @Column(name = "txn_id", nullable = false, updatable = false)
    private UUID txnId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private TxnType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TxnStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, updatable = false)
    private SourceType sourceType;

    @Column(name = "source_ref", nullable = false, updatable = false)
    private String sourceRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_type", nullable = false, updatable = false)
    private DestinationType destinationType;

    @Column(name = "destination_ref", nullable = false, updatable = false)
    private String destinationRef;

    /**
     * Always POSITIVE (paise)
     */
    @Column(name = "txn_amount", nullable = false)
    private long txnAmount;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /* ==============================
       Constructors
       ============================== */

    protected Transaction() {
        // JPA only
    }

    private Transaction(
            TxnType type,
            SourceType sourceType,
            String sourceRef,
            DestinationType destinationType,
            String destinationRef,
            long txnAmount,
            String idempotencyKey
    ) {
        this.type = type;
        this.status = TxnStatus.INITIATED;
        this.sourceType = sourceType;
        this.sourceRef = sourceRef;
        this.destinationType = destinationType;
        this.destinationRef = destinationRef;
        this.txnAmount = txnAmount;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = OffsetDateTime.now();
    }

    /* ==============================
       Factory Method
       ============================== */

    public static Transaction walletToWallet(
            UUID fromWalletId,
            UUID toWalletId,
            long amount,
            String idempotencyKey
    ) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        return new Transaction(
                TxnType.TRANSFER,
                SourceType.WALLET,
                fromWalletId.toString(),
                DestinationType.WALLET,
                toWalletId.toString(),
                amount,
                idempotencyKey
        );
    }

    /* ==============================
       State Transitions (Controlled)
       ============================== */

    public void markSuccess() {
        this.status = TxnStatus.SUCCESS;
    }

    public void markFailed(String reason) {
        this.status = TxnStatus.FAILED;
        this.failureReason = reason;
    }

    /* ==============================
       Getters
       ============================== */

    public UUID getTxnId() {
        return txnId;
    }

    public TxnType getType() {
        return type;
    }

    public TxnStatus getStatus() {
        return status;
    }

    public long getTxnAmount() {
        return txnAmount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /* ==============================
       Enums
       ============================== */

    public enum TxnType {
        ADD,
        TRANSFER,
        PAY,
        BANK_TRANSFER
    }

    public enum TxnStatus {
        INITIATED,
        SUCCESS,
        FAILED
    }

    public enum SourceType {
        WALLET,
        BANK
    }

    public enum DestinationType {
        WALLET,
        BANK,
        MERCHANT
    }
}

