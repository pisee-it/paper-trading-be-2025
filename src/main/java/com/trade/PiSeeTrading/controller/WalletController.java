package com.trade.PiSeeTrading.controller;

import com.trade.PiSeeTrading.dto.response.WalletResponse;
import com.trade.PiSeeTrading.security.services.UserDetailsImpl;
import com.trade.PiSeeTrading.service.impl.WalletServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Wallet Management", description = "APIs for managing user wallets") // Tên nhóm API (Swagger)
public class WalletController {

    WalletServiceImpl walletService;

    // API: Kích hoạt ví Demo $10.000
    @PostMapping("/init-demo")
    public ResponseEntity<WalletResponse> initDemoWallet() {
        Long userId = getCurrentUserId();

        return ResponseEntity.ok(walletService.enableDefaultWallet(userId));
    }

    // API: Xem danh sách ví
    @GetMapping
    public ResponseEntity<List<WalletResponse>> getUserWallet() {
        Long userId = getCurrentUserId();

        return ResponseEntity.ok(walletService.getUserWallets(userId));
    }


    // Helper Method
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ép kiểu Principal về UserDetailsImpl để lấy ID
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getId();
        }

        // Trường hợp không tìm thấy (hiếm xảy ra nếu đã qua Filter Security)
        throw new RuntimeException("Error: User authentication not found in context");
    }
}
