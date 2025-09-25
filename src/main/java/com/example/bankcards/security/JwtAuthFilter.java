package com.example.bankcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwt, UserDetailsService userDetailsService) {
        this.jwt = jwt;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwt.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails ud = userDetailsService.loadUserByUsername(username);
                    if (ud.isAccountNonExpired() && ud.isAccountNonLocked() && ud.isEnabled()) {
                        var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                        auth.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                                .buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ex) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
