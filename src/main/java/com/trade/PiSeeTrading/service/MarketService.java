package com.trade.PiSeeTrading.service;

import java.math.BigDecimal;
import java.util.Map;

public interface MarketService {
    Map<String, BigDecimal> getCurrentPrice(String... coinIds);
    BigDecimal getCoinPrice(String coinId);
}
