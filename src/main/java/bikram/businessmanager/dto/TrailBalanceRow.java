package bikram.businessmanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrailBalanceRow(
        LocalDate date,
        String account,
        String invoiceNo,
        BigDecimal debit,
        BigDecimal credit
) {
}
