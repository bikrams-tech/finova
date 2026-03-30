package bikram.businessmanager.entity.account;

import java.math.BigDecimal;

public class BalanceSheetRow {

    private String groupName;
    private String accountName;
    private BigDecimal debit;
    private BigDecimal credit;

    public BalanceSheetRow(String groupName,
                           String accountName,
                           BigDecimal debit,
                           BigDecimal credit) {
        this.groupName = groupName;
        this.accountName = accountName;
        this.debit = debit;
        this.credit = credit;
    }

    // getters
}
