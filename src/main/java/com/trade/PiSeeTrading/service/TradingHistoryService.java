package com.trade.PiSeeTrading.service;

import com.trade.PiSeeTrading.dto.response.TradeResponse;
import org.springframework.data.domain.Page;

public interface TradingHistoryService {
    Page<TradeResponse> getUserTransactionHistory(Long userId, int page, int size);
}
