package com.trade.PiSeeTrading.repository;

import com.trade.PiSeeTrading.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Tìm tất cả ví của một User
    List<Wallet> findByUserId(Long userId);

    // Tìm ví cụ thể của User theo loại tiền (VD: Lấy ví USDT của User A)
    Optional<Wallet> findByUserIdAndCurrencySymbol(Long userId, String currencySymbol);

    // Kiểm tra xem user đã có ví loại này chưa
    boolean existsByUserIdAndCurrencySymbol(Long userId, String currencySymbol);
}