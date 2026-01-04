package com.trade.PiSeeTrading.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trade.PiSeeTrading.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

//Vấn đề: Spring Security không biết class User của bạn là gì. Nó chỉ làm việc với một interface chuẩn có tên là UserDetails.
//
// Giải pháp: Chúng ta tạo một class UserDetailsImpl để "dịch" User của bạn sang ngôn ngữ mà Spring hiểu.
// Đây là phiên bản bảo mật của User

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsImpl implements UserDetails {
    static final long serialVersionUID = 1L;

    Long id;
    String username;
    String email;

    @JsonIgnore
    String password;

    Collection<? extends GrantedAuthority> authorities;

    // Hàm này dùng để convert từ Entity User sang UserDetailsImpl
    public static UserDetailsImpl build(User user){
        // Convert Role (Enum) sang GrantedAuthority (chuẩn Spring)
        // GrantedAuthority là interface trong Spring Security đại diện cho quyền (role hoặc quyền hạn) mà một người dùng có
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Dự án đơn giản nên return true các hàm dưới
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
