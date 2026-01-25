package com.payment.minipaytm.authentication.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.minipaytm.authentication.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    

}
