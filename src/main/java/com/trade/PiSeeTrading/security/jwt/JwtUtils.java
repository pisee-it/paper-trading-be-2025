package com.trade.PiSeeTrading.security.jwt;

import com.trade.PiSeeTrading.config.JwtProperties;
import com.trade.PiSeeTrading.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT Utility (Bộ công cụ Token)
 * Class này chịu trách nhiệm 3 việc chính:
 *
 * Tạo Token: Khi user login đúng, tạo một cái "vé" (Token) có hạn sử dụng.
 *
 * Giải mã Token: Khi user gửi "vé" lên, đọc xem vé đó của ai.
 *
 * Kiểm soát vé: Kiểm tra xem vé có phải đồ giả hay đã hết hạn chưa.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    // Config từ application.yaml (thêm)
    private final JwtProperties jwtProperties;

    // Tạo token từ thông tin user
    public String generateJwtToken(Authentication authentication) {
        // Lấy thông tin của user đang đăng nhập
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        assert userPrincipal != null;

        // Trả về Jwt với các claims
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // claims sub (Subject) - chủ thể token, thường là user_id hoặc username
                .setIssuedAt(new Date()) // tgian tạo
                .setExpiration(new Date(new Date().getTime() + jwtProperties.getExpiration())) // tgian hết hạn
                .signWith(key(), SignatureAlgorithm.HS512) // Ký tên bằng thuật toán HS512
                .compact();
    }

    // Lấy Username từ token (giải mã)
    public String getUserNameFromJwtToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key()).build().parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Kiểm tra tính hợp lệ của token
    public boolean validateJwtToken(String authToken){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(authToken);
            return true;
        }catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // Helper: Tạo Key mã hóa từ chuỗi Secret trong file config
    // Biến chuỗi secret dài cấu hình trong application.yml thành một object Key chuẩn mật mã học
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }
}
