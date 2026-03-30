package bikram.businessmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record BalanceSheetReport (
    List<BalanceSheetRow> assets,
    List<BalanceSheetRow> liabilities,
    List<BalanceSheetRow> equity,

    BigDecimal totalAssets,
    BigDecimal totalLiabilities,
    BigDecimal totalEquity) {}
