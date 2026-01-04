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
    static BigDecimal INITIAL_BALANCE = new BigDecimal("10000.00000000"); // Vốn khởi điểm cho tài khoản Demo
    static String DEFAULT_CURRENCY = "USDT";

    @Override
    public List<WalletResponse> getUserWallets(Long userId) {
        return walletRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Khởi tạo ví Demo cho người dùng mới.
     * Logic: Chỉ cấp 10,000 USDT nếu ví chưa tồn tại.
     */
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

    /**
     * PHASE 3
     * Core Logic: Cập nhật số dư khi Trading.
     * amount > 0: Cộng tiền (Bán Coin -> Cộng USDT, hoặc Mua Coin -> Cộng Coin)
     * amount < 0: Trừ tiền (Mua Coin -> Trừ USDT, hoặc Bán Coin -> Trừ Coin)
     */
    @Override
    @Transactional
    public Wallet updateBalance(User user, String currency, BigDecimal amount) {
        // 1- Tìm ví hiện tại, nếu chưa có thì tạo
        // Lưu ý: Cần đảm bảo Repository có hàm findByUserIdAndCurrencySymbol trả về Optional
        Wallet wallet = walletRepository.findByUserIdAndCurrencySymbol(user.getId(), currency)
                .orElseGet(() -> {
                    // 2- Nếu ví chưa tồn tại (ví dụ lần đầu mua ETH)

                    // Logic bảo vệ: Không thể TRỪ tiền (Bán) nếu chưa có ví)
                    if (amount.compareTo(BigDecimal.ZERO) < 0){
                        throw new IllegalArgumentException("Cannot sell " + currency + ". Wallet not found");
                    }

                    // Tạo ví mới với số dư 0 (khác với enableDefaultWallet là 10k)
                    // TODO: Có dấu hiệu thừa, bởi đã tạo ví mặc định bằng initDemoWallet()
                    return Wallet.builder()
                            .user(user)
                            .currencySymbol(currency.toUpperCase())
                            .balance(BigDecimal.ZERO)
                            .build();
                });

        // 3- Tính toán số dư mới
        BigDecimal newBalance = wallet.getBalance().add(amount);

        // 4- Kiểm tra số dư âm (Không cho phép nợ)
        if (newBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Insufficient balance for: " + currency);
        }

        // 5- Cập nhật và lưu xuống db
        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);
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

/**
 * Giải thích các thay đổi quan trọng:
 * Xử lý "Lazy Creation" (Tạo ví lười):
 *
 * Khi trading gọi updateBalance để Cộng tiền (Mua coin) -> Nếu ví chưa có, hệ thống tự new Wallet() với balance = 0.
 *
 * Khi trading gọi updateBalance để Trừ tiền (Bán coin) -> Nếu ví chưa có, hệ thống throw Exception ngay (Bạn không thể bán cái bạn chưa có).
 *
 * Return Type:
 *
 * Hàm updateBalance trả về Wallet (Entity) thay vì WalletResponse (DTO). Lý do là TradingService cần Entity để xử lý tiếp logic nội bộ nếu cần, và để đảm bảo tính transaction. Controller sẽ không gọi trực tiếp hàm này.
 *
 * Validate Amount:
 *
 * Thêm đoạn check newBalance < 0 để đảm bảo user không bao giờ xài lố số tiền mình có (Logic quan trọng nhất của Trading).
 */