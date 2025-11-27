package com.trade.PiSeeTrading.service.impl;

import com.trade.PiSeeTrading.dto.response.WalletResponse;
import com.trade.PiSeeTrading.entity.User;
import com.trade.PiSeeTrading.entity.Wallet;
import com.trade.PiSeeTrading.repository.UserRepository;
import com.trade.PiSeeTrading.repository.WalletRepository;
import com.trade.PiSeeTrading.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    WalletRepository walletRepository;
    UserRepository userRepository;

    // Hằng số cấu hình
    static BigDecimal INITIAL_BALANCE = new BigDecimal("10000.00000000");
    static String DEFAULT_CURRENCY = "USDT";

    @Override
    public List<WalletResponse> getUserWallets(Long userId) {
        return walletRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WalletResponse enableDefaultWallet(Long userId) {

        // 1. Kiểm tra xem ví tồn tại chưa
        if (walletRepository.existsByUserIdAndCurrencySymbol(userId, DEFAULT_CURRENCY)){
            Wallet existingWallet = walletRepository.findByUserIdAndCurrencySymbol(userId, DEFAULT_CURRENCY)
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));
            return mapToDTO(existingWallet);
        }

        // 2. Lấy User (Reference để tối ưu)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Tạo ví mới
        Wallet newWallet = Wallet.builder()
                .user(user)
                .currencySymbol(DEFAULT_CURRENCY)
                .balance(INITIAL_BALANCE)
                .build();

        return mapToDTO(walletRepository.save(newWallet));
    }

    // Helper: Convert Entity -> DTO
    private WalletResponse mapToDTO(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .currencySymbol(wallet.getCurrencySymbol())
                .balance(wallet.getBalance())
                .build();
    }
}
