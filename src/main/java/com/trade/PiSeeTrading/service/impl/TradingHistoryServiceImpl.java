package com.trade.PiSeeTrading.service.impl;

import com.trade.PiSeeTrading.dto.response.TradeResponse;
import com.trade.PiSeeTrading.entity.TradeOrder;
import com.trade.PiSeeTrading.repository.TradeOrderRepository;
import com.trade.PiSeeTrading.service.TradingHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TỪ PHASE 4

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TradingHistoryServiceImpl implements TradingHistoryService {

    TradeOrderRepository tradeOrderRepository;

    @Override
    @Transactional(readOnly = true) // dùng readOnly để tối ưu hiệu năng cho các thao tác chỉ đọc db
    public Page<TradeResponse> getUserTransactionHistory(Long userId, int page, int size) {
        // 1. Tạo Pageable object: Page bắt đầu từ 0, sort theo thời gian giảm dần (mới nhất lên đầu)
        // Đây là cách Spring Data JPA hiểu mình muốn lấy trang thứ mấy, mỗi trang bao nhiêu dòng và sắp xếp thế nào.
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        // 2. Gọi Repo query database
        Page<TradeOrder> orderPage = tradeOrderRepository.findByUserId(userId, pageable);

        // 3. Map Entity -> DTO
        // Sử dụng map() của Page để convert từng phần tử
        // Đối tượng Page của Spring rất thông minh. Nó không chỉ chứa danh sách dữ liệu mà còn chứa thông tin meta (tổng số trang, tổng số bản ghi).
        // Hàm map giúp chuyển đổi từng Entity (vật thể dưới DB) sang DTO (vật thể để gửi ra giao diện) mà vẫn giữ nguyên các thông tin phân trang đó.
        return orderPage.map(this::mapToTradeResponse);
    }

    // ---HELPER METHODS---
    private TradeResponse mapToTradeResponse(TradeOrder order) {
        return TradeResponse.builder()
                .orderId(order.getId())
                .symbol(order.getSymbol())
                .orderType(order.getOrderType())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .amount(order.getAmount())
                .timestamp(order.getTimestamp())
                .build();
    }
}