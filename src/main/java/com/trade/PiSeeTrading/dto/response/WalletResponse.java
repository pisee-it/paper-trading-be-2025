package com.trade.PiSeeTrading.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletResponse {
    private Long id;
    private String currencySymbol;
    private BigDecimal balance;
}