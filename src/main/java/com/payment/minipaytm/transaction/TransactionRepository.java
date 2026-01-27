package com.payment.minipaytm.transaction;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction,UUID> {

    /**
     * Idempotency lookup.
     * MUST be indexed + unique at DB level.
     */
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    @Query("""
            Select t
            from Transaction t
            Where t.sourceRef= :walletId
                OR
                t.destinationRef= :walletId
            Order By t.createdAt DESC

    """)
    List<Transaction> findByWalletId(@Param("walletId") String walletId);

      /**
     * Fetch recent transactions (pagination-friendly).
     */
    @Query("""
        SELECT t
        FROM Transaction t
        ORDER BY t.createdAt DESC
    """)
    List<Transaction> findRecent(int limit);
}
