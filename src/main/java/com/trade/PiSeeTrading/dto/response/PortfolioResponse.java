package com.trade.PiSeeTrading.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PortfolioResponse {
    BigDecimal totalBalance; // Tổng tài sản (USDT + Coin Value)
    BigDecimal availableBalance; // Số dư USDT khả dụng để mua tiếp
    List<AssetDTO> assets; // Danh sách chi tiết
}