package com.payment.minipaytm.wallet;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.payment.minipaytm.ledger.LedgerEntry;
import com.payment.minipaytm.ledger.LedgerEntryRepository;
import com.payment.minipaytm.transaction.Transaction;
import com.payment.minipaytm.transaction.TransactionRepository;
import com.payment.minipaytm.transaction.TransferResult;

import jakarta.transaction.Transactional;

@Service
public class WalletTransferService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final TransactionRepository transactionRepository;


    // wallet Transfer Constructor
    public WalletTransferService(
        WalletRepository walletRepository,
        LedgerEntryRepository ledgerEntryRepository,
        TransactionRepository transactionRepository
    ){
        this.walletRepository=walletRepository;
        this.ledgerEntryRepository=ledgerEntryRepository;
        this.transactionRepository=transactionRepository;
    }

    /**
     * wallet-> wallet Transfer
     */
    @Transactional
     public TransferResult transfer(UUID fromWalletId,
            UUID toWalletId,
            UUID requesterUserId,
            long amount,
            String idempotencyKey
    ){
        /*
        1. Basic validation
        */
        if(amount<=0){
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (fromWalletId.equals(toWalletId)) {
            throw new IllegalArgumentException("Self transfer is not allowed");
        }


         /* =============================
           2. Idempotency check
           ============================= */
        Optional<Transaction> existing =
                transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            return TransferResult.from(existing.get());
        }

        /* =============================
           3. Lock wallets (deadlock-safe)
           ============================= */
        UUID first  = fromWalletId.compareTo(toWalletId) < 0 ? fromWalletId : toWalletId;
        UUID second = fromWalletId.compareTo(toWalletId) < 0 ? toWalletId : fromWalletId;

        List<Wallet> wallets =
                walletRepository.findAllByIdForUpdate(List.of(first, second));

        Wallet fromWallet = wallets.stream()
                .filter(w -> w.getWalletId().equals(fromWalletId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Source wallet not found"));

        Wallet toWallet = wallets.stream()
                .filter(w -> w.getWalletId().equals(toWalletId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Destination wallet not found"));

        /* =============================
           4. Ownership & balance checks
           ============================= */
        if (!fromWallet.getUserId().equals(requesterUserId)) {
            throw new SecurityException("User does not own source wallet");
        }

        if (fromWallet.getBalanceCache() < amount) {
            throw new IllegalStateException("Insufficient balance");
        }

        /* =============================
           5. Create transaction (INITIATED)
           ============================= */
        Transaction txn = Transaction.walletToWallet(
                fromWalletId,
                toWalletId,
                amount,
                idempotencyKey
        );
        transactionRepository.save(txn);

        try {
            /* =============================
               6. Apply debit / credit
               ============================= */
            long fromBalanceAfter = fromWallet.getBalanceCache() - amount;
            long toBalanceAfter   = toWallet.getBalanceCache() + amount;

            fromWallet.debit(amount);
            toWallet.credit(amount);

            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            /* =============================
               7. Ledger entries (immutable)
               ============================= */
            LedgerEntry debitEntry = LedgerEntry.debit(
                    txn.getTxnId(),
                    fromWalletId,
                    amount,
                    fromBalanceAfter
            );

            LedgerEntry creditEntry = LedgerEntry.credit(
                    txn.getTxnId(),
                    toWalletId,
                    amount,
                    toBalanceAfter
            );

            ledgerEntryRepository.saveAll(List.of(debitEntry, creditEntry));
        /* =============================
               8. Mark transaction SUCCESS
               ============================= */
            txn.markSuccess();
            transactionRepository.save(txn);

            return TransferResult.success(
                    txn.getTxnId(),
                    fromBalanceAfter,
                    toBalanceAfter
            );

        } catch (Exception e) {
            /* =============================
               9. Failure handling
               ============================= */
            txn.markFailed(e.getMessage());
            transactionRepository.save(txn);
            throw e;
        }

    }

    public String  walletRecharge(UUID userId,UUID walletId,Long amount){
         /*
        1. Basic validation
        */
        if(amount<=0){
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

         

        Wallet walletOpt = walletRepository.findByWalletIdAndUserId(walletId,userId).orElseThrow(()-> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Wallet not found"
                                    ));
            
        walletOpt.credit(amount);
        walletRepository.save(walletOpt);
        return "Wallet recharged successfully. New balance: " + walletOpt.getBalanceCache();
        
    }
}
