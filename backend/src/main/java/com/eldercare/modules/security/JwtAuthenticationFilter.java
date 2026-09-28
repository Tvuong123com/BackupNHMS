package com.eldercare.modules.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SessionStore sessionStore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            Optional<SessionDetails> sessionOpt = sessionStore.getSession(token);

            if (sessionOpt.isPresent()) {
                SessionDetails session = sessionOpt.get();
                sessionStore.updateActivity(token);

                String role = session.getRole();
                java.util.List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.replace(" ", "_")));
                    authorities.add(new SimpleGrantedAuthority(role));
                    // Grant admin access for system admin/nha admin/admin
                    if (role.equalsIgnoreCase("System_Administrator") || role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("NHA_ADMIN") || role.contains("Admin")) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_System_Administrator"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_NHA_Admin"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_NHA_ADMIN"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_DON"));
                    }
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        session.getEmail(),
                        null,
                        authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
