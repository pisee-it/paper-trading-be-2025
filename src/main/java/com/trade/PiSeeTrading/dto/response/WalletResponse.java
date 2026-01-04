package com.trade.PiSeeTrading.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class WalletResponse {
    private Long id;
    private String currencySymbol;
    private BigDecimal balance;
    // Không trả về User object đầy đủ, chỉ cần thiết thì trả về ID
}
