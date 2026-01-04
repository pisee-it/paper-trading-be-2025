package com.trade.PiSeeTrading.service.impl;

import com.trade.PiSeeTrading.dto.response.AssetDTO;
import com.trade.PiSeeTrading.dto.response.PortfolioResponse;
import com.trade.PiSeeTrading.entity.Wallet;
import com.trade.PiSeeTrading.repository.WalletRepository;
import com.trade.PiSeeTrading.service.PortfolioService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PortfolioServiceImpl implements PortfolioService {

    WalletRepository walletRepository;
    MarketServiceImpl marketService;

    @Override
    public PortfolioResponse getPortfolio(Long userId) {

        // 1- Lấy Wallet từ DB
        // Kết quả trả về là một List các object Wallet
        List<Wallet> userWallets = walletRepository.findByUserId(userId);

        // 2- Khởi tạo các biến tính toán

        // Khi đi làm, sàn có thể có nhiều loại tiền ổn định như USDC, BUSD... lúc này availableBalance sẽ là tổng của chúng.
        BigDecimal availableBalanceUSDT = BigDecimal.ZERO; // Số lượng USDT (vì đây là tiền tệ có thể trao đổi lấy coin)
        BigDecimal totalAssetValue = BigDecimal.ZERO; // Tổng giá trị các coin đang giữ
        List<AssetDTO> assetList = new ArrayList<>(); // Danh sách chi tiết để hiển thị

        // 3- Duyệt qua từng ví để tính toán
        for (Wallet w : userWallets) {
            String symbol = w.getCurrencySymbol();
            BigDecimal quantity = w.getBalance();

            // Case A: Nếu là USDT -> đây là tiền mặt khả dụng
            if (symbol.equals("USDT")) {
                availableBalanceUSDT = quantity;
                continue; // bỏ qua, không cần gọi API lấy giá (vì 1 USDT ~ 1 USD)
            }

            // Case B: Nếu là Coin (BTC, ETH...) và số lượng > 0
            if (quantity.compareTo(BigDecimal.ZERO) > 0){
                // Gọi MarketService để lấy giá thị trường hiện tại (Realtime price)
                BigDecimal currentPrice = marketService.getCoinPrice(symbol);

                // Tính giá trị: Value = số lượng * giá hiện tại
                BigDecimal value = quantity.multiply(currentPrice);

                // Cộng dồn vào tổng giá trị tài sản coin
                totalAssetValue = totalAssetValue.add(value);

                // Thêm vào danh sách hiển thị chi tiết cho Client
                assetList.add(AssetDTO.builder()
                        .symbol(symbol)
                        .quantity(quantity)
                        .currentPrice(currentPrice)
                        .totalValue(value)
                        .build());
            }
        }

        // 4- Tổng tài sản ròng (Net Worth) = Tiền mặt (USDT) + Giá trị Coin
        BigDecimal totalNetWorth = availableBalanceUSDT.add(totalAssetValue);

        // 5- Trả về DTO
        return PortfolioResponse.builder()
                .totalBalance(totalNetWorth) // Tổng tài sản
                .availableBalance(availableBalanceUSDT) // Số dư mua được
                .assets(assetList) // Danh sách coin đang giữ
                .build();
    }
}
