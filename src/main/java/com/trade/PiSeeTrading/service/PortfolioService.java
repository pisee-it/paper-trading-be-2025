package com.trade.PiSeeTrading.service;

import com.trade.PiSeeTrading.dto.response.PortfolioResponse;

public interface PortfolioService {
    PortfolioResponse getPortfolio(Long userId);


}
