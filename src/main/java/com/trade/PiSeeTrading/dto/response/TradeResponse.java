package com.trade.PiSeeTrading.dto.response;

import com.trade.PiSeeTrading.entity.OrderType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class TradeResponse {
    Long orderId;
    String symbol;
    OrderType orderType;
    BigDecimal quantity;
    BigDecimal price; // giá khớp lệnh
    BigDecimal amount; // Tổng tiền trừ/cộng
    LocalDateTime timestamp;
}
