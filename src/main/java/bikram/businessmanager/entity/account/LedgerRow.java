package bikram.businessmanager.entity.account;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LedgerRow {

    private LocalDate date;
    private String voucher;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balance;

}
