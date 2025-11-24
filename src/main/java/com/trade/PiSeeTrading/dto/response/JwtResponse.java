package com.trade.PiSeeTrading.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtResponse {
    String token;
    String type = "Bearer";

    Long id;
    String username;
    String email;
    String role; // trả về role để frontend biết điều hướng (admin/user)

    public JwtResponse(String token, Long id, String username, String email, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
