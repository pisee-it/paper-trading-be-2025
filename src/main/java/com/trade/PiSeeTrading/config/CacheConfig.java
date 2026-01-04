package com.trade.PiSeeTrading.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // TODO: Thay thế bằng Redis
    @Bean
    public CacheManager cacheManager() {
        // Khởi tạo bộ quản lý Cache lưu trong RAM (ConcurrentMap)
        // Quan trọng: Phải khai báo tên cache "cryptoPrices" trùng với tên trong @Cacheable ở getCurrentPrice thuộc MarketServiceImpl
        return new ConcurrentMapCacheManager("cryptoPrices");
    }
}
