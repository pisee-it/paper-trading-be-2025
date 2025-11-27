package com.trade.PiSeeTrading.service;

import com.trade.PiSeeTrading.dto.response.WalletResponse;

import java.util.List;

public interface WalletService {
    // Lấy danh sách ví của User
    List<WalletResponse> getUserWallets(Long userId);

    // Tạo ví mặc định (USDT) cho User
    WalletResponse enableDefaultWallet(Long userId);
}
