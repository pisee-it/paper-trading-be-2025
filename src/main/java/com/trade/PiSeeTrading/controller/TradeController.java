package com.trade.PiSeeTrading.controller;

import com.trade.PiSeeTrading.dto.request.TradeRequest;
import com.trade.PiSeeTrading.dto.response.TradeResponse;
import com.trade.PiSeeTrading.entity.User;
import com.trade.PiSeeTrading.repository.UserRepository;
import com.trade.PiSeeTrading.service.impl.TradingHistoryServiceImpl;
import com.trade.PiSeeTrading.service.impl.TradingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
@Tag(name = "Trading API", description = "Các API liên quan đến đặt lệnh mua bán")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TradeController {
    TradingServiceImpl tradingService;
    UserRepository userRepository;
    TradingHistoryServiceImpl tradingHistoryService;

    // PHASE 3
    @PostMapping
    @Operation(summary = "Đặt lệnh Mua/Bán (Place Order",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TradeResponse> placeOrder(@Valid @RequestBody TradeRequest request){
        // 1- Lấy thông tin người dùng hiện tại từ Security Context (JWT)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assert authentication != null;
        String currentUsername = authentication.getName(); // lấy username/email từ Token

        // 2- Tìm User ID thực tế trong DB (đảm bảo lệnh trade được thực hiện đúng bởi chủ sở hữu Token)
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // 3- Gọi Service xử lý
        TradeResponse response = tradingService.placeOrder(user.getId(), request);

        return ResponseEntity.ok(response);

    }


    // PHASE 4 - XEM LỊCH SỬ GIAO DỊCH
    @GetMapping
    public ResponseEntity<Page<TradeResponse>> getHistory (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        // 1. Lấy user hiện tại từ Token
        User user = getCurrentUser();

        // 2. Gọi Service lấy lịch sử
        return ResponseEntity.ok(
                tradingHistoryService.getUserTransactionHistory(user.getId(), page, size)
        );
    }



    // ---PRIVATE HELPER METHODS---
    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra kỹ - Trường hợp chưa đăng nhập (Optional)
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Authenticated user not found");
        }

        String currentUsername = authentication.getName();

        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
