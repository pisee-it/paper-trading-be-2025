package com.trade.PiSeeTrading.service.impl;

import com.trade.PiSeeTrading.service.MarketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketServiceImpl implements MarketService {
    RestTemplate restTemplate = new RestTemplate(); // "trình gửi request" (GET/POST/PUT/DELETE) thay vì gọi bằng Postman

    // URL CoinGecko
    @Value("${app.coingecko.url}")
    String coingeckoApiUrl;

    @Override
    @Cacheable(value = "cryptoPrices", key = "#coinIds[0]") // Cache kết quả - key đơn giản là phần tử đầu tiên.
    public Map<String, BigDecimal> getCurrentPrice(String... coinIds) {
        log.info("---CALLING EXTERNAL API (COINGECKO) ---"); // Log kiểm tra việc hoạt động của Cache

        String ids = String.join(",", coinIds);

        // BuildURL: ?ids=bitcoin,ethereum&vs_currencies=usd
        String url = UriComponentsBuilder.fromUriString(coingeckoApiUrl)
                .queryParam("ids", ids)
                .queryParam("vs_currencies", "usd")
                .toUriString();

        try{
            // Gọi API và map kết quả về Map<String, Map<String, Double>>
            // Cấu trúc JSON trả về:
            /*

            {
                "ethereum": 2826.4,
                "bitcoin": 86345
            }

             */
            Map response = restTemplate.getForObject(url, Map.class);

            Map<String, BigDecimal> result = new HashMap<>();

            if (response != null){
                for (String coinId : coinIds) {
                    Map<String, Object> prices = (Map<String, Object>) response.get(coinId);
                    if (prices != null && prices.containsKey("usd")){
                        // Convert Object -> String -> BigDecimal để an toàn
                        Object priceObj = prices.get("usd");
                        result.put(coinId, new BigDecimal(priceObj.toString()));
                    }
                }
            }
            return result;
        } catch (Exception e){
            log.error("Error calling CoinGecko API: {}", e.getMessage());
            // Fallback: Trả về map rỗng hoặc thrơ exception tuỳ logic
            return Map.of();
        }
    }
}
