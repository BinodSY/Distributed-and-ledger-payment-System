package com.payment.minipaytm.wallet;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    /**
     * Fetch wallet by userId (no lock).
     * Used for read-only operations.
     */
    Optional<Wallet> findByUserId(UUID userId);

    /**
     * Lock a single wallet row FOR UPDATE.
     * Used in money movement.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.walletId = :walletId")
    Optional<Wallet> findByIdForUpdate(@Param("walletId") UUID walletId);

    /**
     * Lock multiple wallets FOR UPDATE in a single query.
     * IMPORTANT: caller must ensure consistent ordering
     * to avoid deadlocks.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.walletId IN :walletIds")
    List<Wallet> findAllByIdForUpdate(@Param("walletIds") List<UUID> walletIds);
}