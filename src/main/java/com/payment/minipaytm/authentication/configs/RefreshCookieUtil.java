package com.payment.minipaytm.authentication.configs;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RefreshCookieUtil {

    public ResponseCookie buildRefreshCookie(String name,String value,long maxAgeSeconds){
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // true in production (HTTPS)
                .path("/auth/refresh")
                .sameSite("Strict")
                .maxAge(maxAgeSeconds)
                .build();
    }

    public ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .sameSite("Strict")
                .maxAge(0)
                .build();
    }
    public String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
