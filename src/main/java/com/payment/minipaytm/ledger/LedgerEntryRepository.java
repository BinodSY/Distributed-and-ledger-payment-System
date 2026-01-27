package com.payment.minipaytm.ledger;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry,UUID> {
    /**
     * Fetch ledger entries for a wallet in chronological order.
     * Used for audit, debugging, reconciliation.
     */
    @Query("""
        SELECT l
        FROM LedgerEntry l
        WHERE l.walletId = :walletId
        ORDER BY l.createdAt ASC
    """)
    List<LedgerEntry> findByWalletId(@Param("walletId") UUID walletId);

    /**
     * Fetch ledger entries for a transaction.
     * For SUCCESS txn → must return exactly 2 entries.
     */
    @Query("""
        SELECT l
        FROM LedgerEntry l
        WHERE l.txnId = :txnId
    """)
    List<LedgerEntry> findByTxnId(@Param("txnId") UUID txnId);

    /**
     * Get last ledger entry for wallet.
     * Used for sanity checks and balance verification.
     */
    @Query("""
        SELECT l
        FROM LedgerEntry l
        WHERE l.walletId = :walletId
        ORDER BY l.createdAt DESC
        LIMIT 1
    """)
    LedgerEntry findLatestByWalletId(@Param("walletId") UUID walletId);

}
