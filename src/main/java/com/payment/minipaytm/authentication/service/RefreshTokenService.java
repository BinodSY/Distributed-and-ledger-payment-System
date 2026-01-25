package com.payment.minipaytm.authentication.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payment.minipaytm.authentication.configs.JwtUtil;
import com.payment.minipaytm.authentication.configs.TokenHashUtil;
import com.payment.minipaytm.authentication.dto.RefreshResult;
import com.payment.minipaytm.authentication.model.RefreshToken;
import com.payment.minipaytm.authentication.repository.RefreshTokenRepository;
import com.payment.minipaytm.user.model.User;
import com.payment.minipaytm.user.reposistory.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {
        
    private final RefreshTokenRepository repo;
    private final long tokenDays;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private  UserRepository userRepository;


    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repo,@Value("${jwt.refresh.tokenday}") long tokenDays) {
        this.repo = repo;
        this.tokenDays = tokenDays;
    }

    public String generateRawRefreshToken(){
        byte [] bytes=new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public String createAndStore(UUID userId){
        String raw=generateRawRefreshToken();
        String hash=TokenHashUtil.sha256Hex(raw);

        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setTokenHash(hash);
        rt.setExpiresAt(Instant.now().plus(tokenDays, ChronoUnit.DAYS));
        rt.setRevoked(false);

        repo.save(rt);

        return raw;
    }

    public RefreshToken validatRefreshToken(String rawRefreshtoken){
        String hash = TokenHashUtil.sha256Hex(rawRefreshtoken);

        RefreshToken rt=repo.findByTokenHash(hash)
                            .orElseThrow(()->new RuntimeException("Invalid Refresh token"));
    
        if(rt.isRevoked()) throw new RuntimeException("Refresh Token revoked");
        if (rt.getExpiresAt().isBefore(Instant.now())) throw new RuntimeException("Refresh token expired");

        rt.setLastUsedAt(Instant.now());
        repo.save(rt);
        return rt;

    }

    @Transactional
    public String rotateToken(RefreshToken oldToken){
        oldToken.setRevoked(true);
        repo.save(oldToken);
         // create new
        return createAndStore(oldToken.getUserId());
    }

    @Transactional
    public void revoke(String rawRefreshetoken){
        String hash=TokenHashUtil.sha256Hex(rawRefreshetoken);
        repo.findByTokenHash(hash).ifPresent(rt->{
            rt.setRevoked(true);
            repo.save(rt);
        });
    }

    @Transactional
    public RefreshResult refresh(String rawRefreshToken){
        //validate refresh token
        RefreshToken refreshToken=validatRefreshToken(rawRefreshToken);

        //Grant new Refresh token in case expire
        String newRefresh=rotateToken(refreshToken);
        //find user by id to issue new refresh token
        User user = userRepository.findByUserId(refreshToken.getUserId());
        if (user == null) throw new RuntimeException("User not found");

        //give new access/JWT Token 
        String newAccess = jwtUtil.generateToken(user.getEmail(), user.getUserId());
        RefreshResult result = new RefreshResult();

        //return resposne DTo
        result.setAccessToken(newAccess);
        result.setNewRefreshToken(newRefresh);
        result.setEmail(user.getEmail());
        result.setExpiresInSeconds(Duration.ofDays(7).toSeconds()); // match config
        return result;
    }
    }

