package com.trade.PiSeeTrading.controller;

import com.trade.PiSeeTrading.dto.response.PortfolioResponse;
import com.trade.PiSeeTrading.entity.User;
import com.trade.PiSeeTrading.repository.UserRepository;
import com.trade.PiSeeTrading.service.impl.PortfolioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Portfolio API", description = "Quản lý danh mục đầu tư & tài sản")
public class PortfolioController {

    PortfolioServiceImpl portfolioService;
    UserRepository userRepository;

    @GetMapping
    // @Operation sử dụng khi có Swagger, ự động tạo ra một trang tài liệu giao diện giúp người khác hiểu được API.
    // TODO: Tìm hiểu về Operation
    @Operation(summary = "Xem Portfolio (Tổng tài sản và danh mục coin)",
                description = "Trả về tổng số dư USDT, tổng giá trị tài sản quy đổi và danh sách chi tiết coin đang nắm giữ",
                security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PortfolioResponse> getPortfolio() {
        // 1- Lấy thông tin user đang đăng nhập
        User user = getAuthenticatedUser();

        // 2- Gọi Service tính toán Portfolio
        PortfolioResponse response = portfolioService.getPortfolio(user.getId());

        return ResponseEntity.ok(response);
    }

    // Hàm private helper để lấy User từ Token (Clean Code: Tách biệt logic xác thực)
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized: User is not authenticated");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in database"));
    }

}