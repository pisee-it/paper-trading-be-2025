package com.trade.PiSeeTrading.controller;


import com.trade.PiSeeTrading.service.impl.MarketServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarketController {

    MarketServiceImpl  marketService;

    // API: Lấy giá Coin
    // GET /api/market/prices?ids=bitcoin,ethereum
    // defaultValue dùng để tránh lỗi thiếu param
    @GetMapping("/prices")
    public ResponseEntity<?> getPrices(@RequestParam(defaultValue = "bitcoin,ethereum") String ids) {
        String[] coinList = ids.split(",");
        return ResponseEntity.ok(marketService.getCurrentPrice(coinList));
    }

}
