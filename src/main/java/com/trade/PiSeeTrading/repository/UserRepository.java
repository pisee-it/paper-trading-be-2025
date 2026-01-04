package com.trade.PiSeeTrading.repository;

import com.trade.PiSeeTrading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    // For Login
    Optional<User> findByUsername(String username);

    // For Register
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

/**
 * Với Optional: Hàm sẽ trả về Optional<User>.
 * Cái tên này "ép" người đồng nghiệp (hoặc chính bạn) phải hiểu rằng: "Dữ liệu này có thể có hoặc không nhé, hãy xử lý cẩn thận!".
 *
 * Optional là một cái "hộp" (container) dùng để bao bọc một đối tượng. Nó có thể chứa một giá trị non-null, hoặc không chứa gì cả (empty).
 * Đây là "vũ khí" cực mạnh để giúp bạn tránh lỗi NullPointerException (NPE) — cơn ác mộng của mọi lập trình viên.
 *
 * Các phương thức
 *
 * - Optional.ofNullable(value): Tạo một Optional có thể chứa giá trị hoặc null.
 *
 * - isPresent(): Trả về true nếu có giá trị.
 *
 * - ifPresent(consumer): Nếu có giá trị thì thực hiện một hành động nào đó (ví dụ: in ra màn hình).
 *
 * - orElse(defaultValue): Nếu rỗng thì lấy một giá trị mặc định.
 *
 * - orElseThrow(): Nếu rỗng thì ném ra một ngoại lệ (Exception) do bạn chỉ định.
 */