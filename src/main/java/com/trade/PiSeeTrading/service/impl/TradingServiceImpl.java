package com.trade.PiSeeTrading.service.impl;

import com.trade.PiSeeTrading.dto.request.TradeRequest;
import com.trade.PiSeeTrading.dto.response.TradeResponse;
import com.trade.PiSeeTrading.entity.OrderType;
import com.trade.PiSeeTrading.entity.TradeOrder;
import com.trade.PiSeeTrading.entity.User;
import com.trade.PiSeeTrading.repository.TradeOrderRepository;
import com.trade.PiSeeTrading.repository.UserRepository;
import com.trade.PiSeeTrading.service.TradingService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <b>ACID Transaction (Tính toàn vẹn dữ liệu)</b>
 * <p>
 * ACID là viết tắt của 4 tính chất quan trọng nhằm đảm bảo dữ liệu luôn chính xác
 * khi thực hiện một chuỗi các hành động liên tiếp (Database Transaction).
 * </p>
 *
 * <h3>4 Tính chất cốt lõi:</h3>
 * <ul>
 * <li><b>A - Atomicity (Tính nguyên tử):</b> "Tất cả hoặc không gì cả". Một giao dịch bao gồm nhiều bước.
 * Nếu 1 bước thất bại, toàn bộ các bước trước đó phải bị hủy bỏ (Rollback).</li>
 * <li><b>C - Consistency (Tính nhất quán):</b> Dữ liệu trước và sau giao dịch phải luôn thỏa mãn các luật lệ
 * (Ví dụ: Số dư không được âm, Khóa ngoại phải tồn tại).</li>
 * <li><b>I - Isolation (Tính cô lập):</b> Nếu có 2 người cùng đặt lệnh một lúc, hệ thống phải xử lý
 * như thể họ đang xếp hàng lần lượt, không để dữ liệu của người này làm sai lệch người kia.</li>
 * <li><b>D - Durability (Tính bền vững):</b> Khi giao dịch đã báo "Thành công" (Commit), dữ liệu sẽ được lưu vĩnh viễn
 * vào ổ cứng, dù hệ thống có bị mất điện ngay sau đó.</li>
 * </ul>
 *
 * <h3>Ví dụ áp dụng: Chức năng Mua Coin (Trading)</h3>
 * <pre>
 * Quy trình:
 * 1. Trừ 100 USDT ví A.
 * 2. Cộng 1 BTC ví A.
 * 3. Lưu lịch sử giao dịch.
 * </pre>
 *
 * <table border="1">
 * <tr>
 * <th>Trường hợp</th>
 * <th>Không dùng ACID (@Transactional)</th>
 * <th>Có dùng ACID (@Transactional)</th>
 * </tr>
 * <tr>
 * <td><b>Kịch bản</b></td>
 * <td>Bước 1 thành công (Trừ tiền). <br> Bước 2 bị lỗi (Sập mạng/Code bug).</td>
 * <td>Bước 1 thành công (Tạm thời). <br> Bước 2 bị lỗi.</td>
 * </tr>
 * <tr>
 * <td><b>Hậu quả</b></td>
 * <td>Tiền USDT bị trừ mất, nhưng BTC không được cộng. <br> -> <b>MẤT TIỀN (Data Corruption)</b>.</td>
 * <td>Hệ thống phát hiện lỗi ở Bước 2. <br> -> Tự động <b>ROLLBACK</b> (Hoàn tác) Bước 1. <br> -> Tiền USDT quay về như cũ.</td>
 * </tr>
 * <tr>
 * <td><b>Kết quả</b></td>
 * <td>Dữ liệu sai lệch nghiêm trọng.</td>
 * <td>Dữ liệu an toàn tuyệt đối.</td>
 * </tr>
 * </table>
 *
 * @see org.springframework.transaction.annotation.Transactional
 */

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TradingServiceImpl implements TradingService {

    TradeOrderRepository tradeOrderRepository;
    UserRepository userRepository;
    WalletServiceImpl walletService;
    MarketServiceImpl marketService;

    @Override
    @Transactional(rollbackOn = Exception.class) // Rollback nếu có bất kỳ lỗi nào - ACID Transaction
    public TradeResponse placeOrder(Long userId, TradeRequest tradeRequest) {
        // 1- Validate User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2- Validate input cơn bản (số lượng coin giao dịch)
        if (tradeRequest.getQuantity().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // [FIX QUAN TRỌNG] Chuẩn hóa Symbol thành chữ in hoa ngay từ đầu
        // Từ giờ code sẽ dùng biến 'symbol' này thay vì chỉ tradeRequest.getSymbol()
        String symbol = tradeRequest.getSymbol().toUpperCase();

        // 3- Lấy giá thị trường (Real-time Price) (Dùng biến symbol đã upper case)
        // Lấy hàm getCoin ở MarketServiceImpl
        Map<String, BigDecimal> priceMap = marketService.getCurrentPrice(symbol);

        // Lấy giá từ Map ra. Nếu Map rỗng hoặc không có key -> Lỗi
        BigDecimal currentPrice = priceMap.get(symbol.toLowerCase()); // Lưu ý: Map của CoinGecko trả về key thường

        // Fix logic lấy giá từ Map: CoinGecko đôi khi trả key thường, đôi khi key hoa.
        // Để chắc chắn, check cả 2.
        if (currentPrice == null) {
            currentPrice = priceMap.get(symbol); // Thử tìm key hoa
        }
        if (currentPrice == null) {
            // Fallback cuối cùng: Lấy value đầu tiên trong map nếu map size = 1
            if (!priceMap.isEmpty()) {
                currentPrice = priceMap.values().iterator().next();
            } else {
                throw new IllegalArgumentException("Unable to fetch price for symbol: " + symbol);
            }
        }

        // 4- Tính toán tổng tiền (Amount = Price * Quantity)
        BigDecimal totalAmount = currentPrice.multiply(tradeRequest.getQuantity());


        log.info("User {} placing {} order for {} {} at price {}",
                userId, tradeRequest.getOrderType(), tradeRequest.getQuantity(), tradeRequest.getSymbol(), totalAmount);

        // 5- Xử lý Logic Giao dịch (Gọi WalletService để trừ/cộng tiền) (Truyền symbol in hoa vào)
        if (tradeRequest.getOrderType() == OrderType.BUY){
            handleBuy(user, symbol, tradeRequest.getQuantity(), totalAmount);
        } else if (tradeRequest.getOrderType() == OrderType.SELL){
            handleSell(user, symbol, tradeRequest.getQuantity(), totalAmount);
        }

        // 6- Lưu lịch sử giao dịch (Record Trade)
        TradeOrder tradeOrder = TradeOrder.builder()
                .user(user)
                .symbol(symbol) // đã uppercase
                .orderType(tradeRequest.getOrderType())
                .quantity(tradeRequest.getQuantity())
                .price(currentPrice)
                .amount(totalAmount)
                .build();

        TradeOrder savedTradeOrder = tradeOrderRepository.save(tradeOrder);

        // 7- Trả về kqua
        return mapToTradeResponse(savedTradeOrder);
    }

    // ----PRIVATE HELP METHODS----

    // Logic Buy (Trừ USDT -> Cộng Coin)
    private void handleBuy(User user, String symbol, BigDecimal quantity, BigDecimal totalCost){
        // Trừ USDT (TotalCost là số dương -> negate() thành âm để trừ
        walletService.updateBalance(user, "USDT", totalCost.negate());

        // Cộng Coin vào ví (Quantity là số dương -> cộng)
        walletService.updateBalance(user, symbol, quantity);
    }

    // Logic Sell (Trừ Coin -> Cộng USDT)
    private void handleSell(User user, String symbol, BigDecimal quantity, BigDecimal totalEarned){
        // Trừ Coin khỏi ví (Quantity là số dương -> negate() thành âm để trừ)
        walletService.updateBalance(user, symbol, quantity.negate());

        // Cộng USDT vào ví (TotalEarned là số dương -> cộng)
        walletService.updateBalance(user, "USDT", totalEarned);
    }

    private TradeResponse mapToTradeResponse(TradeOrder tradeOrder){
        return TradeResponse.builder()
                .orderId(tradeOrder.getId())
                .symbol(tradeOrder.getSymbol())
                .orderType(tradeOrder.getOrderType())
                .quantity(tradeOrder.getQuantity())
                .price(tradeOrder.getPrice())
                .amount(tradeOrder.getAmount())
                .timestamp(tradeOrder.getTimestamp())
                .build();
    }
}