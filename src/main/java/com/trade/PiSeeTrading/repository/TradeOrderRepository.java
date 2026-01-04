package com.trade.PiSeeTrading.repository;

import com.trade.PiSeeTrading.entity.TradeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeOrderRepository extends JpaRepository<TradeOrder,Long> {
    // Để lấy lịch sử giao dịch của user sau này
    // Tuy nhiên, Nếu User có 10.000 giao dịch, DB sẽ load toàn bộ 10.000 dòng này vào RAM một lúc -> Gây chậm hệ thống hoặc lỗi OutOfMemory
    // Không sử dụng List
    //    List<TradeOrder> findByUserIdOrderByTimestampDesc(Long userId);

    // TỪ PHASE 4
    // Sử dụng phân trang (Pagination) bắt buộc cho các tính năng "Lịch sử"
    // JPA sẽ tự động tạo câu query phân trang
    // TODO: Học về phân trang
    Page<TradeOrder> findByUserId(Long userId, Pageable pageable);
}
