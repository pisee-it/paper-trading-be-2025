package com.trade.PiSeeTrading.service;

import com.trade.PiSeeTrading.dto.request.TradeRequest;
import com.trade.PiSeeTrading.dto.response.TradeResponse;

public interface TradingService {
     TradeResponse placeOrder(Long userId, TradeRequest tradeRequest);
}
