package com.trade.PiSeeTrading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Set timeout cho CoinGecko
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory(); // Khởi tạo Factory
        factory.setConnectTimeout(5000); // Timeout kết nối: 5s - nếu CoinGecko sập, app không bị treo
        factory.setReadTimeout(5000); // Timeout đọc dữ liệu: 5s
        return new RestTemplate(factory);
    }

}