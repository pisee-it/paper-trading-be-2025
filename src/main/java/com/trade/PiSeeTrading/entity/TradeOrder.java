package com.trade.PiSeeTrading.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với User (Người đặt lệnh)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Mã coin, ví dụ: "bitcoin", "ethereum"
    @Column(nullable = false)
    private String symbol;

    // Loại lệnh: BUY hoặc SELL
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    // Số lượng coin giao dịch (Ví dụ: 0.5 BTC)
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    // Giá tại thời điểm khớp lệnh (Ví dụ: 50000.00 USDT/BTC)
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal price;

    // Tổng giá trị giao dịch = quantity * price (Ví dụ: 25000.00 USDT)
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal amount;

    // Thời gian khớp lệnh
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
