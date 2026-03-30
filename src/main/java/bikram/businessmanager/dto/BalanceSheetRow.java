package bikram.businessmanager.dto;

import java.math.BigDecimal;

public record BalanceSheetRow(
        String accountName,
        String category,
        BigDecimal amount
) implements ReportRowInterface {
    @Override
    public String[] getColumns() {
        return new String[]{accountName,category};
    }
}
