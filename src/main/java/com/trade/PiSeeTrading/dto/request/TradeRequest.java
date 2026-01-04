package com.trade.PiSeeTrading.dto.request;

import com.trade.PiSeeTrading.entity.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRequest {

    @NotBlank(message = "Symbol is required")
    private String symbol; // e.g., "bitcoin"

    @NotNull(message = "Order type is required")
    private OrderType orderType; // BUY or SELL

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;
}