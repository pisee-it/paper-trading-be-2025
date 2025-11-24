package com.trade.PiSeeTrading.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class JwtProperties {
    // Hai thuộc tính này hứng giá trị tương ứng từ application.yaml
    String secret;
    long expiration;
}