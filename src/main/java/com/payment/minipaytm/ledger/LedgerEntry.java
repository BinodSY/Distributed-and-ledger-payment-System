package com.payment.minipaytm.ledger;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "ledger_entries",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_ledger_txn_wallet_entry",
            columnNames = {"txn_id", "wallet_id", "entry_type"}
        )
    },
    indexes = {
        @Index(name = "idx_ledger_wallet", columnList = "wallet_id"),
        @Index(name = "idx_ledger_txn", columnList = "txn_id")
    }
)
public class LedgerEntry {

    @Id
    @GeneratedValue
    @Column(name = "ledger_id", nullable = false, updatable = false)
    private UUID ledgerId;

    @Column(name = "txn_id", nullable = false, updatable = false)
    private UUID txnId;

    @Column(name = "wallet_id", nullable = false, updatable = false)
    private UUID walletId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, updatable = false)
    private EntryType entryType;

    /**
     * Always POSITIVE.
     * Direction is defined by entryType (DEBIT / CREDIT).
     */
    @Column(name = "amount", nullable = false)
    private long amount;

    /**
     * Wallet balance AFTER applying this ledger entry.
     * Used for fast verification and audits.
     */
    @Column(name = "balance_after", nullable = false)
    private long balanceAfter;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /* ==============================
       Constructors
       ============================== */

    protected LedgerEntry() {
        // JPA only
    }

    private LedgerEntry(
            UUID txnId,
            UUID walletId,
            EntryType entryType,
            long amount,
            long balanceAfter
    ) {
        this.txnId = txnId;
        this.walletId = walletId;
        this.entryType = entryType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = OffsetDateTime.now();
    }

    /* ==============================
       Factory Methods (IMPORTANT)
       ============================== */

    public static LedgerEntry debit(
            UUID txnId,
            UUID walletId,
            long amount,
            long balanceAfter
    ) {
        validate(amount, balanceAfter);
        return new LedgerEntry(txnId, walletId, EntryType.DEBIT, amount, balanceAfter);
    }

    public static LedgerEntry credit(
            UUID txnId,
            UUID walletId,
            long amount,
            long balanceAfter
    ) {
        validate(amount, balanceAfter);
        return new LedgerEntry(txnId, walletId, EntryType.CREDIT, amount, balanceAfter);
    }

    private static void validate(long amount, long balanceAfter) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Ledger amount must be positive");
        }
        if (balanceAfter < 0) {
            throw new IllegalStateException("Ledger balance_after cannot be negative");
        }
    }

    /* ==============================
       Getters (NO setters)
       ============================== */

    public UUID getLedgerId() {
        return ledgerId;
    }

    public UUID getTxnId() {
        return txnId;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public long getAmount() {
        return amount;
    }

    public long getBalanceAfter() {
        return balanceAfter;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /* ==============================
       Enum
       ============================== */

    public enum EntryType {
        DEBIT,
        CREDIT
    }
}
