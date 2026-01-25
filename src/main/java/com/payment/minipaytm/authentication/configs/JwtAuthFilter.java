package com.payment.minipaytm.authentication.configs;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

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

                    // 1) If no token, just continue (public endpoints will work)
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
                                String email=jwtutil.extractEmail(token);

                                if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){

                                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,null,List.of());

                                    SecurityContextHolder.getContext().setAuthentication(authToken);
                                }

        filterChain.doFilter(request, response);



    }

}
