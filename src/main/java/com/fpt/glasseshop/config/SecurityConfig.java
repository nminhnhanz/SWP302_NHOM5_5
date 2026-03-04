package com.fpt.glasseshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/**", "/register/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                        // Swagger/OpenAPI docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Public Product APIs
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products/**").permitAll()

                        // Admin Only: Full access to Products, Users, and Order deletion
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/products/**")
                        .hasRole("ADMIN")

                        // Staff & Admin: View all orders and update status
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/orders")
                        .hasAnyRole("OPERATIONAL_STAFF", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/orders/**")
                        .hasAnyRole("OPERATIONAL_STAFF", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/orders/**")
                        .hasAnyRole("OPERATIONAL_STAFF", "ADMIN")

                        // Customer & Authenticated: Checkout, Cart, Own Orders
                        .requestMatchers("/api/cart/**").authenticated()
                        .requestMatchers("/api/orders/checkout").authenticated()
                        .requestMatchers("/api/orders/user/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/orders/{id}").authenticated()
                        .requestMatchers("/api/payments/**").authenticated()

                        .requestMatchers("/operational-staff/**").hasAnyRole("OPERATIONAL_STAFF", "ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}
