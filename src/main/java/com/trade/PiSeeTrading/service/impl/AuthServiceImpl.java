package com.trade.PiSeeTrading.service.impl;

import com.trade.PiSeeTrading.dto.request.LoginRequest;
import com.trade.PiSeeTrading.dto.request.RegisterRequest;
import com.trade.PiSeeTrading.dto.response.JwtResponse;
import com.trade.PiSeeTrading.entity.User;
import com.trade.PiSeeTrading.entity.UserRole;
import com.trade.PiSeeTrading.repository.UserRepository;
import com.trade.PiSeeTrading.security.jwt.JwtUtils;
import com.trade.PiSeeTrading.security.services.UserDetailsImpl;
import com.trade.PiSeeTrading.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    AuthenticationManager authenticationManager; // sẽ cấu hình ở phase 4
    UserRepository userRepository;
    PasswordEncoder passwordEncoder; // sẽ cấu hình ở phase 4
    JwtUtils jwtUtils;

    @Override
    public void registerUser(RegisterRequest registerRequest) {
        // Kiểm tra tồn tại
        if (userRepository.existsByUsername(registerRequest.getUsername())){
            throw new RuntimeException("Username is already in use");
        }
        else if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new RuntimeException("Email is already in use");
        }

        // Tạo User Entity mới
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword())) // mã hoá mật khẩu
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .role(UserRole.ROLE_USER) // mặc định là user
                .enabled(true)
                .build();

        // Lưu vào DB
        userRepository.save(user);

        // TODO: Thêm logic tạo Ví tiền mặc định
    }

    // Đăng nhập
    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // Xác thực qua Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // Nếu login thành công, set vào context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Sinh JWT Token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Lấy thông tin UserDetails để trả về front-end
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        assert userDetails != null;
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority())
                .orElse("ROLE_USER");

        return new JwtResponse(jwt,
                               userDetails.getId(),
                               userDetails.getUsername(),
                               userDetails.getEmail(),
                               role);
    }
}
