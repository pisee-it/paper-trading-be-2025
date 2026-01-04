package com.trade.PiSeeTrading.service.impl;

import com.trade.PiSeeTrading.integration.CoinGeckoClient;
import com.trade.PiSeeTrading.service.MarketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarketServiceImpl implements MarketService {

    // Inject Client vào thay vì dùng RestTemplate trực tiếp như cũ (vì thấy constructor -> truy ngược container -> inject đúng bean, ko cần Autowired).
    // Triển khai từ bước tìm hiểu về FACADE
    CoinGeckoClient coinGeckoClient;

    @Override
//    @Cacheable(value = "cryptoPrices", key = "#coinIds[0]") // Cache kết quả - key đơn giản là phần tử đầu tiên (value: tên cache, key: )
    @Cacheable(value = "cryptoPrices", key = "#coinIds")
    public Map<String, BigDecimal> getCurrentPrice(String... coinIds) {
        log.info("--- [FACADE] Requesting prices for: {} ---", Arrays.toString(coinIds));

        // Gọi Client để lấy dữ liệu giá
        Map<String, BigDecimal> prices = coinGeckoClient.fetchPrices(coinIds);

        // Tại đây có thể thêm logic Fallback (nếu prices rỗng thì gọi sàn khác)
        // Nhưng hiện tại ta chỉ return
        return prices;
    }

    // Lấy giá của 1 coin duy nhất. Đóng vai trò Wrapper để PortfolioService dễ sử dụng hơn
    @Override
    public BigDecimal getCoinPrice(String coinId) {
        // 1- Tái sử dụng logic lấy giá
        Map<String, BigDecimal> prices = coinGeckoClient.fetchPrices(coinId);

        // 2- Extract giá trị từ Map, xử lý Null Safety
        if (prices == null || !prices.containsKey(coinId)) {
            log.warn("Price not found for: {}. Defaulting to 0.", coinId);
            return BigDecimal.ZERO;
        }

        return prices.get(coinId);
    }
}

/**
 * SỬ DỤNG SỨC MẠNH CỦA FACADE PATTERN ĐỂ TÁCH LOGIC API CLIENT SERVER (TẦNG KẾT NỐI) RIÊNG, SERVICE LAYER (TẦNG NGHIỆP VỤ) RIÊNG.
 */