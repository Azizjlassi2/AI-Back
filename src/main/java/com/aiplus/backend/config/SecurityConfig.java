package com.aiplus.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aiplus.backend.auth.jwt.JwtAuthenticationFilter;
import com.aiplus.backend.auth.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> {
        }).csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/auth/login").permitAll().requestMatchers("/api/v1/auth/register")
                        .permitAll().requestMatchers("/api/v1/auth/forgot-password").permitAll()
                        .requestMatchers("/api/v1/auth/forgot-password/reset").permitAll()
                        .requestMatchers("/api/v1/auth/reset-password").permitAll()
                        .requestMatchers("/api/v1/auth/update-password").authenticated()

                        .requestMatchers("/api/v1/favorites/models").authenticated()
                        .requestMatchers("/api/v1/favorites/models/*").authenticated()

                        .requestMatchers("/api/v1/users").permitAll()

                        .requestMatchers("/api/v1/users/*").permitAll() // only for debugging

                        .requestMatchers("/api/v1/tasks").permitAll().requestMatchers("/api/v1/models").permitAll()
                        .requestMatchers("/api/v1/models/{id}").permitAll().requestMatchers("/v1/models/**").permitAll()
                        .requestMatchers("/api/v1/models/developer/{id}").permitAll()
                        .requestMatchers("/api/v1/contact/us").authenticated().requestMatchers("/api/v1/models/publish")
                        .hasRole("DEVELOPER").requestMatchers("/api/v1/payments/**").permitAll()
                        .requestMatchers("/api/v1/subscriptions").authenticated()

                        .requestMatchers("/api/v1/subscriptions/**").permitAll().requestMatchers("/api/v1/developer/**")
                        .hasRole("DEVELOPER").requestMatchers("/api/v1/deployments/**").permitAll().anyRequest()
                        .authenticated());

        // Add JWT token filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}