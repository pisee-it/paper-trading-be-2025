package com.trade.PiSeeTrading.controller;

import com.trade.PiSeeTrading.dto.response.WalletResponse;
import com.trade.PiSeeTrading.security.services.UserDetailsImpl; // Import chính xác class bạn vừa gửi
import com.trade.PiSeeTrading.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet Management", description = "APIs for managing user wallets") // Tên nhóm API
public class WalletController {

    private final WalletService walletService;

    // API: Kích hoạt ví Demo $10,000
    // POST /api/wallets/init-demo
    @PostMapping("/init-demo")
    @Operation(summary = "Create Demo Wallet", description = "Creates a default USDT wallet with $10,000 balance") // Mô tả API
    public ResponseEntity<WalletResponse> initDemoWallet() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(walletService.enableDefaultWallet(userId));
    }

    // API: Xem danh sách ví
    // GET /api/wallets
    @GetMapping
    public ResponseEntity<List<WalletResponse>> getMyWallets() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(walletService.getUserWallets(userId));
    }

    // --- HELPER METHOD (ĐÃ CHUẨN HÓA) ---
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ép kiểu Principal về UserDetailsImpl để lấy ID
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getId();
        }

        // Trường hợp không tìm thấy (thường hiếm khi xảy ra nếu đã qua Filter Security)
        throw new RuntimeException("Error: User authentication not found in context.");
    }
}