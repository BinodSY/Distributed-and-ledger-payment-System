package com.payment.minipaytm.controller;


import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.minipaytm.authentication.dto.CustomUserPrincipal;
import com.payment.minipaytm.transaction.TransferResult;

import com.payment.minipaytm.wallet.WalletService;
import com.payment.minipaytm.wallet.WalletTransferService;
import com.payment.minipaytm.wallet.DTO.BalanceReq;
import com.payment.minipaytm.wallet.DTO.WalletTransferRequest;
import com.payment.minipaytm.wallet.DTO.WalletTransferResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final WalletTransferService walletTransferService;
    private final WalletService walletService;
    public WalletController(WalletTransferService walletTransferService, WalletService walletService){
        this.walletTransferService=walletTransferService;
        this.walletService=walletService;
    }

    @PostMapping("/transfer")
    public ResponseEntity <WalletTransferResponse> transfer(
        @AuthenticationPrincipal CustomUserPrincipal principal,
        @RequestBody @Valid WalletTransferRequest request){
            
            TransferResult result = walletTransferService.transfer(
                request.fromWalletId(),
                request.toWalletId(),
                principal.userId(),
                request.amount(),
                request.idempotencyKey()
        );

        return ResponseEntity.ok(
                new WalletTransferResponse(
                        result.txnId(),
                        result.status(),
                        result.fromWalletBalance(),
                        result.toWalletBalance()
                )
        );
    }

    @PostMapping("/create")
    public ResponseEntity<?> walletCreate(@AuthenticationPrincipal CustomUserPrincipal principal){
        UUID walletId=walletService.createWallet(principal.userId());

        return ResponseEntity.status(HttpStatus.CREATED).body(walletId.toString());
    }

    @PostMapping("/me")
        public ResponseEntity<?> balanceCheck(@RequestBody BalanceReq balanceReq,@AuthenticationPrincipal CustomUserPrincipal principal){
            Long  balance=walletService.getBalance(balanceReq.walletId(),principal.userId());
            return ResponseEntity.ok(Map.of(
                "walletId", balanceReq.walletId(),
                "balance", balance
            ));
        }
    
}
