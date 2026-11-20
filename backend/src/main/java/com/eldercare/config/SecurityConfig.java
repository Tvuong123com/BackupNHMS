package com.eldercare.config;

import com.eldercare.modules.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/auth/me", "/api/v1/auth/me").authenticated()
                .requestMatchers("/auth/**", "/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, WebsiteAiRateLimitFilter.PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers("/api/v1/ai/settings", "/api/v1/ai/settings/**", "/api/v1/ai/models")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/ai/search/reindex")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON")
                .requestMatchers("/api/v1/ai/risk/fall-screenings", "/api/v1/ai/risk/fall-screenings/**")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON", "NURSE")
                .requestMatchers(HttpMethod.GET, "/api/v1/ai/risk/review-priorities")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON", "NURSE")
                .requestMatchers(HttpMethod.POST, "/api/v1/ai/careplan/suggest",
                        "/api/v1/ai/medication/check", "/api/v1/ai/elopement/evaluate")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON", "NURSE")
                .requestMatchers(HttpMethod.GET, "/api/v1/ai/search")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON", "NURSE")
                .requestMatchers("/api/v1/ai/summary/**", "/api/v1/ai/handover/**")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON", "NURSE")
                .requestMatchers(HttpMethod.POST, "/api/v1/care-plans", "/api/v1/care-plans/*/goals", "/api/v1/care-plans/*/goals/**")
                    .authenticated()
                .requestMatchers("/api/v1/pre-admissions/**", "/api/v1/admissions/**", "/api/v1/admin/facility-settings/select")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON", "NURSE", "Admission_Staff")
                .requestMatchers("/api/v1/ai/**")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON", "NURSE", "CNA")
                .requestMatchers("/api/v1/demo-data-seeder/**")
                    .hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN")
                .requestMatchers("/admin/**", "/api/v1/admin/**", "/api/v1/care-plans/**", "/api/v1/tasks/**", "/api/v1/**").hasAnyRole("System_Administrator", "ADMIN", "NHA_Admin", "NHA_ADMIN", "DON", "NURSE", "CNA")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new WebsiteAiRateLimitFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
