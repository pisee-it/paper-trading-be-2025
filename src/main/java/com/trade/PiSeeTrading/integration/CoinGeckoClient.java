package com.trade.PiSeeTrading.integration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// Chỉ Giao tiếp với CoinGecko. Không Cache, không Business logic - Triển khai từ bước tìm hiểu về FACADE

@Component // Bean xử lý Integration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CoinGeckoClient {
//    final RestTemplate restTemplate = new RestTemplate();

    @Autowired // Factory Method tại AppConfig
    RestTemplate restTemplate;

    @Value("${app.coingecko.url}")
    String coingeckoApiUrl;


    // Nhận danh sách coinIds → gọi API CoinGecko /simple/price → lấy giá USD hiện tại → trả về Map<coinId, BigDecimal>
    public Map<String, BigDecimal> fetchPrices(String... coinIds) {

        // 1. Ghép danh sách coinId thành chuỗi: "bitcoin,ethereum"
        String ids = String.join(",", coinIds);

        // 2. Build URL gọi CoinGecko API
        // Ví dụ: /simple/price?ids=bitcoin,ethereum&vs_currencies=usd
        String url = UriComponentsBuilder.fromUriString(coingeckoApiUrl)
                .queryParam("ids", ids)
                .queryParam("vs_currencies", "usd")
                .toUriString();

        log.info("--- [CLIENT] CALLING COINGECKO API: {} ---", url);

        try {
            // 3. Fake header để giả lập trình duyệt (tránh 403 Forbidden)
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            headers.add("Accept", "application/json");

            // Truyền headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 4. Gọi API bằng exchange để gửi kèm Header
            // Response JSON sẽ map thành Map<String, Object>
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,     // CoinGecko chỉ cho phép GET
                    entity,
                    Map.class
            );

            Map response = responseEntity.getBody();

            // 5. Khai báo Map chứa kết quả cuối: <coinId, price>
            Map<String, BigDecimal> result = new HashMap<>();

            if (response != null) {
                for (String coinId : coinIds) {

                    // CoinGecko trả key dạng lowercase (bitcoin, ethereum)
                    Map<String, Object> coinData =
                            (Map<String, Object>) response.get(coinId.toLowerCase());

                    if (coinData != null && coinData.containsKey("usd")) {

                        // Lấy giá USD
                        Object priceObj = coinData.get("usd");

                        // Convert sang BigDecimal để đảm bảo chính xác tiền tệ
                        result.put(coinId, new BigDecimal(priceObj.toString()));
                    }
                }
            }

            log.info("Fetched prices: {}", result);
            return result;

        } catch (Exception e) {
            // 6. Log lỗi khi gọi API (403, 429, timeout...)
            log.error("Error calling CoinGecko API", e);

            // 7. Fallback: trả map rỗng (tránh crash hệ thống)
            return Map.of();
        }
    }
}
