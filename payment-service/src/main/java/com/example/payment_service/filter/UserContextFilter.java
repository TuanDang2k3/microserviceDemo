package com.example.payment_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String userId = request.getHeader("X-User-Id");
        
        if (userId != null && !userId.isEmpty()) {
            // Set userId vào SecurityContext
            UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Set user context for userId: {}", userId);
        }
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clear SecurityContext sau khi request hoàn thành
            SecurityContextHolder.clearContext();
        }
    }
}
