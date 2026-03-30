package bikram.businessmanager.dto;

import java.math.BigDecimal;



import bikram.businessmanager.entity.account.AccountType;
public record ProfitandLossDto(
        Long subAccountId,
        String accountName,
        AccountType accountType,
        BigDecimal amount
) implements ReportRowInterface {
    @Override
    public String[] getColumns() {
        return new String[]{subAccountId.toString(),accountName,accountType.toString(),amount.toString()};
    }
}