package com.trade.PiSeeTrading.service;

import java.math.BigDecimal;
import java.util.Map;

public interface MarketService {
    // Lấy giá của danh sách coin (VD bitcoin, ethereum)
    Map<String, BigDecimal> getCurrentPrice (String... coinIds); // String... là một mảng String[]
}
