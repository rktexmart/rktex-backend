package com.opsmonsters.quick_bite.Configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityFilter {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowedOrigins}")
    private String[] allowedOrigins;

    public SecurityFilter(
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ allow static resources & uploads without login
                        .requestMatchers(
                                "/uploads/**",
                                "/images/**",
                                "/error/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/products/**"
                        ).permitAll()

                        // ✅ public APIs (GET only)
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/{userId}/reviews").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/{userId}/average-rating").permitAll()
                        .requestMatchers(
                                "/auth/**",
                                "/api/auth/**",
                                "/auth/users/register",
                                "/otp/**",
                                "/cart/**",
                                "/orders/**",
                                "/wishlist/**",
                                "/payments/**",
                                "/api/payment/**",
                                "/payment/**",
                                "/quickbite/product/images/**",
                                "/api/product-attributes/**"
                        ).permitAll()

                        // ✅ Reviews
                        .requestMatchers(HttpMethod.POST, "/users/{userId}/reviews").authenticated()

                        // ✅ Admin only APIs
                        .requestMatchers("/actuator/**").hasRole("ACTUATOR")
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/promo/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/promo/{promoId}").hasRole("ADMIN")

                        // ✅ Promo APIs — Public
                        .requestMatchers(HttpMethod.POST, "/api/promo/apply-direct").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/promo/apply-cart").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/promo/**").permitAll()

                        // ✅ everything else requires authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public InMemoryUserDetailsManager actuatorUser() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("Admin@123")
                .roles("ACTUATOR")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
