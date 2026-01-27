package com.payment.minipaytm.authentication.configs;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.payment.minipaytm.authentication.dto.CustomUserPrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtutil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                // Implementation will go here
                String header=request.getHeader(("Authorization"));
                System.out.println("Authorization header = " + header);

                    // 1 If no token, just continue (public endpoints will work)
                    if (header == null || !header.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                        String token=header.substring(7);
                        
                        // 3) Validate token
                            if (!jwtutil.validateToken(token)) {
                                filterChain.doFilter(request, response);
                                return;
                            }   
                                UUID userId=jwtutil.extractUserId(token);
                                String email=jwtutil.extractEmail(token);
                                CustomUserPrincipal principal =
                                         new CustomUserPrincipal(userId,email,"ROLE_USER");

                                if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){

                                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principal,null,List.of(new SimpleGrantedAuthority("ROLE_USER")));

                                    SecurityContextHolder.getContext().setAuthentication(authToken);
                                }

        filterChain.doFilter(request, response);



    }

}
