package com.trade.PiSeeTrading.config;

import com.trade.PiSeeTrading.security.jwt.AuthEntryPointJwt;
import com.trade.PiSeeTrading.security.jwt.AuthTokenFilter;
import com.trade.PiSeeTrading.security.jwt.JwtUtils;
import com.trade.PiSeeTrading.security.services.UserDetailsServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Đây là nơi "cấp phép" cho các Bean mà chúng ta thiếu ở Giai đoạn 3 (AuthenticationManager, PasswordEncoder).
 *
 * Lưu ý kỹ thuật: Vì bạn đã bỏ @Component ở AuthTokenFilter và dùng Constructor Injection, chúng ta sẽ khai báo nó là một @Bean thủ công ở đây.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // cho phép dùng @PreAuthorize ở Controller sau này
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    UserDetailsServiceImpl userDetailsService;
    AuthEntryPointJwt unauthorizedHandler;
    JwtUtils jwtUtils;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình AuthenticationManager - được AuthService dùng để login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }

    // Cấu hình Provider - Liên kết UserDetailsServiceImpl ới PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. KÍCH HOẠT CORS: Dòng này cực kỳ quan trọng, nó báo cho Spring Security dùng cấu hình corsConfigurationSource bên dưới
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // tắt csrf vì đang dùng token (stateless)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // xử lý lỗi 401
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // không lưu session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Cho phép truy cập công khai vào API Login/Register
                        .requestMatchers("/api/test/**").permitAll() // Cho phép test api (nếu có)
                        .anyRequest().authenticated()); // Tất cả API khác đều phải có Token mới được vào

        // Thêm Provider
        http.authenticationProvider(authenticationProvider());

        // Thêm Filter của chúng ta trước UsernamePasswordAuthenticationFilter của Spring
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. CẤU HÌNH CORS CHUẨN: Thay vì trả về CorsFilter, ta trả về CorsConfigurationSource
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép Frontend Angular gọi
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Cho phép đầy đủ các method
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));

        // Cho phép mọi header (bao gồm Authorization, Content-Type...)
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép gửi credentials (nếu cần cookie sau này)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
