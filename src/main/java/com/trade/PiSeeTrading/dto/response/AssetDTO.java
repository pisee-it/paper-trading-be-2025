package com.trade.PiSeeTrading.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AssetDTO {
    private String symbol;
    private BigDecimal quantity;      // Số lượng đang sở hữu
    private BigDecimal currentPrice;  // Giá thị trường hiện tại
    private BigDecimal totalValue;    // quantity * currentPrice
}
