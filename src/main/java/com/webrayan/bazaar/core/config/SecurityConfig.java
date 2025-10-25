package com.webrayan.bazaar.core.config;

import com.webrayan.bazaar.core.security.CustomPermissionEvaluator;
import com.webrayan.bazaar.core.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomPermissionEvaluator permissionEvaluator;
    
    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/auth/**", "/profile/**", "/login", "/logout", "/", "/ads/**", "/test/**", "/api/oauth2/**", "/api/favorites/**")
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/auth/**", "/login", "/ads/**", "/test/**", "/api/oauth2/**").permitAll()
                .requestMatchers("/api/favorites/**").authenticated()
                .requestMatchers("/profile/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/profile", true)
                .failureUrl("/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .securityMatcher("/api/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .securityMatcher(request -> {
                String path = request.getServletPath();
                return path.startsWith("/api/") && !path.startsWith("/api/favorites/") && !path.startsWith("/api/oauth2/")
                    || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs/") 
                    || path.startsWith("/swagger-resources/") || path.startsWith("/webjars/");
            })
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/common/countries/**").permitAll()
                .requestMatchers("/api/common/settings/public/**").permitAll()
                .requestMatchers("/api/common/tags/**").permitAll()
                
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/acl/users/**").hasRole("SYSTEM_ADMIN")
                .requestMatchers("/api/acl/roles/**").hasRole("SYSTEM_ADMIN")
                .requestMatchers("/api/common/settings/**").hasRole("SYSTEM_ADMIN")
                
                // Seller endpoints
                .requestMatchers("/api/catalog/shops/**").hasAnyRole("SELLER", "SYSTEM_ADMIN")
                .requestMatchers("/api/catalog/products/**").hasAnyRole("SELLER", "SYSTEM_ADMIN")
                
                // Customer endpoints
                .requestMatchers("/api/sales/carts/**").hasAnyRole("CUSTOMER", "SELLER", "SYSTEM_ADMIN")
                .requestMatchers("/api/sales/orders/**").hasAnyRole("CUSTOMER", "SELLER", "SYSTEM_ADMIN")
                
                // Ads endpoints
                .requestMatchers("/api/ads/**").hasAnyRole("CUSTOMER", "SELLER", "SYSTEM_ADMIN")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
                .referrerPolicy(referrerPolicy -> 
                    referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
