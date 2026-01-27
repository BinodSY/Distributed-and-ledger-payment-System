package com.payment.minipaytm.wallet;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    WalletService(WalletRepository walletRepository){
        this.walletRepository=walletRepository;
    }
    @Transactional
    public UUID createWallet(UUID userId) {
        Optional<Wallet> existing = walletRepository.findByUserId(userId);
            if (existing.isPresent()) {
                return existing.get().getWalletId(); // idempotent behavior
            }

    Wallet wallet = new Wallet(userId);
    Wallet saved = walletRepository.save(wallet);

    return saved.getWalletId();

    }
}
