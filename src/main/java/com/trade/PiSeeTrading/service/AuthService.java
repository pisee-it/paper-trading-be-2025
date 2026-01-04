package com.trade.PiSeeTrading.service;

import com.trade.PiSeeTrading.dto.request.LoginRequest;
import com.trade.PiSeeTrading.dto.request.RegisterRequest;
import com.trade.PiSeeTrading.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}
