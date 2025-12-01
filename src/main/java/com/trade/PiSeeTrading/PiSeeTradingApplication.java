package com.trade.PiSeeTrading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching // kích hoạt cache: do CoinGecko giới hạn số lần call, bắt buộc phải lưu cache trong 1-2p. F5 liên tục sẽ lấy giá từ cache thay vì CoinGecko để tránh chặn IP.
public class PiSeeTradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiSeeTradingApplication.class, args);
	}

}
