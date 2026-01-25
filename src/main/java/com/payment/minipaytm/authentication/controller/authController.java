package com.payment.minipaytm.authentication.controller;



import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.minipaytm.authentication.configs.CookieUtil;
import com.payment.minipaytm.authentication.configs.RefreshCookieUtil;
import com.payment.minipaytm.authentication.dto.RefreshResult;
import com.payment.minipaytm.authentication.dto.loginRequest;
import com.payment.minipaytm.authentication.dto.loginRes;
import com.payment.minipaytm.authentication.dto.regRes;
import com.payment.minipaytm.authentication.dto.registerReq;
import com.payment.minipaytm.authentication.service.RefreshTokenService;
import com.payment.minipaytm.authentication.service.authService;
import com.payment.minipaytm.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/auth")
public class authController {

    @Autowired
    private authService authservice;

    @Autowired
    private UserService userService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private RefreshTokenService refreshService;
    @Autowired
    private RefreshCookieUtil refreshCookieUtil;

    
    @GetMapping("/health-check")
    public String healthCheck(){
        return "application is running fine";
    }

    

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginRequest request,HttpServletResponse response){
        String email=request.getEmail();
        String password=request.getPassword();
        loginRes loginres=authservice.login(email,password);

          long maxAgeSeconds = Duration.ofDays(7).toSeconds();

        ResponseCookie cookie = refreshCookieUtil.buildRefreshCookie(
                "refreshToken",
                loginres.getRefreshToken(),
                maxAgeSeconds
        );

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // IMPORTANT: remove refresh token from body
        loginres.setRefreshToken(null);

        return ResponseEntity.ok(loginres);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request,HttpServletResponse response){
        String rawRefresh=cookieUtil.getCookieValue(request,"refreshToken");
         if (rawRefresh == null || rawRefresh.isBlank()) {
            return ResponseEntity.status(401).body("Missing refresh token");
        }
        RefreshResult result = refreshService.refresh(rawRefresh);

        ResponseCookie cookie = refreshCookieUtil.buildRefreshCookie(
                "refreshToken",
                result.getNewRefreshToken(),
                result.getExpiresInSeconds()
        );

         response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // return only access token + user info
        loginRes res = new loginRes();
        res.setToken(result.getAccessToken());
        res.setEmail(result.getEmail());
        res.setExpiresInSeconds(result.getExpiresInSeconds());

        return ResponseEntity.ok(res);
    }
    
    


    @PostMapping("/register")
    public regRes register(@RequestBody registerReq request){
        return userService.registerUser(request);
    }
}
