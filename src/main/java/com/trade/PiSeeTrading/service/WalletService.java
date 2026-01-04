package com.trade.PiSeeTrading.service;

import com.trade.PiSeeTrading.dto.response.WalletResponse;
import com.trade.PiSeeTrading.entity.User;
import com.trade.PiSeeTrading.entity.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    List<WalletResponse> getUserWallets(Long userId);
    WalletResponse enableDefaultWallet(Long userId);
    Wallet updateBalance(User user, String currency, BigDecimal amount);
}
