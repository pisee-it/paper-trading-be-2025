package com.trade.PiSeeTrading.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "currency_symbol"}) // Một User chỉ có 1 ví cho 1 loại tiền (VD: 1 ví USDT)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với User Entity (đã có ở Phase 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "currency_symbol", nullable = false, length = 10)
    private String currencySymbol; // Ví dụ: USDT, BTC, ETH

    // QUAN TRỌNG: Precision cho Crypto/Money
    // Đại diện cho số lượng đơn vị của một loại tài sản cụ thể trong một chiếc ví (vd bao nhiêu đồng BTC, bao nhiêu đồng USDT)
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal balance;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist // gthich tai https://techmaster.vn/posts/37554/vong-doi-cua-jpa-entity-va-cac-event-lien-quan
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}