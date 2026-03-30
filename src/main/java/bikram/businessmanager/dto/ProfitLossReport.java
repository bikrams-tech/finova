package bikram.businessmanager.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProfitLossReport(
        List<ProfitandLossDto> rows,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netProfit
) {

}
