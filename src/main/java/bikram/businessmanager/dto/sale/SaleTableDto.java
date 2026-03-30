package bikram.businessmanager.dto.sale;

import bikram.businessmanager.entity.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleTableDto(
        Long id,
        String invoiceNo,
        String customerName,
        PaymentMethod paymentMethod,
        Integer total_item,
        BigDecimal grand_total,
        LocalDateTime saleDate
) {}