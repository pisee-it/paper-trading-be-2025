package com.trade.PiSeeTrading.security.services;

import com.trade.PiSeeTrading.entity.User;
import com.trade.PiSeeTrading.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Đây là Service mà Spring Security sẽ gọi khi ai đó đăng nhập.
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tim user trong db
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found (không tìm được user) with username: " + username));

        // Convert sang UserDetailsImpl
        return UserDetailsImpl.build(user);
    }
}

/**
 * @Transactional là annotation trong Spring dùng để quản lý giao dịch (transaction) cho các phương thức hoặc lớp.
 *
 * Nó đảm bảo rằng tất cả các thao tác trong một giao dịch sẽ được thực hiện đầy đủ hoặc không thực hiện gì (atomicity):
 *
 * Nếu thành công, thay đổi được commit vào database.
 *
 * Nếu có lỗi, toàn bộ thay đổi sẽ được rollback (hoàn tác).
 */
