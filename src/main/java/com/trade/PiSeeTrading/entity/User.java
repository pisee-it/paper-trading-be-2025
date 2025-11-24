package com.trade.PiSeeTrading.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder // Lombok: Giúp khởi tạo object theo kiểu Builder pattern
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String username;

    @Column(unique = true, nullable = false)
    String email;

    @Column(name = "password_hash", nullable = false)
    String password;

    @Column(name = "full_name")
    String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    UserRole role = UserRole.ROLE_USER;

    @Column(name = "refresh_token")
    String refreshToken;

    @Column(nullable = false)
    boolean enabled = true;

    // Audit Fields: Tu dong luu thoi gian
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    // QUAN HỆ VỚI WALLET (Tạm thời comment lại để code không báo lỗi vì chưa có class Wallet)
    // Sau khi tạo class Wallet xong, chúng ta sẽ mở comment này ra.
    /*
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wallet> wallets;
    */
}
