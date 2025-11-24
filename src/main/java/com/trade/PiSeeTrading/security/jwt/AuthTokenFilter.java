package com.trade.PiSeeTrading.security.jwt;

// (Người gác cổng) - đứng chắn trước mọi request vào API.
// Logic là: khách gõ cửa (req) -> class hỏi có mang token ko -> Nếu có, đưa cho JwtUtils ktra -> Nếu chuẩn, báo cho Spring Security cho qua

import com.trade.PiSeeTrading.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthTokenFilter extends OncePerRequestFilter {
    JwtUtils jwtUtils;
    UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtUtils = jwtUtils;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Lấy chuỗi JWT từ Header request
            String jwt = parseJwt(request);

            // Nếu có JWT và JWT hợp lệ
            if (jwt != null && jwtUtils.validateJwtToken(jwt)){
                // Lấy thông tin user đầy đủ từ Database
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Lấy thông tin user đầy đủ từ Database
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

                // Tạo object Authentication để thông báo cho Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // LƯU THÔNG TIN USER VÀO SECURITY CONTEXT (QUAN TRỌNG NHẤT)
                // Từ giờ trở đi, Spring Security biết user này đã login.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication", e);
        }

        // Cho phép request đi tiếp vào các Filter khác hoặc vào Controller
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Cắt bỏ chữ "Bearer " để lấy token
        }
        return null;
    }
}
