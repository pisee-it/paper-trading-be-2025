package com.trade.PiSeeTrading.service;

import com.trade.PiSeeTrading.dto.request.LoginRequest;
import com.trade.PiSeeTrading.dto.request.RegisterRequest;
import com.trade.PiSeeTrading.dto.response.JwtResponse;

public interface AuthService {
    void registerUser(RegisterRequest registerRequest);
    JwtResponse authenticateUser(LoginRequest loginRequest);
}
