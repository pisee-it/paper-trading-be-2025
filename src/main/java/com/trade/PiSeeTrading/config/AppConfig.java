package com.trade.PiSeeTrading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class AppConfig {

    // Đây là factory method
    // Factory Method là phương thức chuyên dùng để tạo object, thay vì tạo bằng new trực tiếp ở khắp nơi
    // Nó được dùng khi nơi khác sử dụng @Autowired
    @Bean
    public RestTemplate restTemplate() {
        // Set timeout cho CoinGecko
        // RestTemplate không trực tiếp gọi HTTP, mà ủy quyền cho factory
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory(); // Khởi tạo Factory (Là Request Factory, Tạo HTTP connection, Gửi request, Nhận response)
        factory.setConnectTimeout(5000); // Nếu sau 5s không kết nối được → throw exception, tránh CoinGecko sập → web đứng hình mãi
        factory.setReadTimeout(5000); // Kết nối đã thành công nhưng server xử lý lâu, ko trả về kqua -> throw exception.

        return new RestTemplate(factory); // Tạo RestTemplate với factory đã cấu hình
    }

}