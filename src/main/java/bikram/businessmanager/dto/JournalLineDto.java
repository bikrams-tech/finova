package bikram.businessmanager.dto;

import bikram.businessmanager.entity.account.SubAccount;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class JournalLineDto {

    private LocalDate date;
    private String voucherNo;
    private String note;
    private SubAccountDto subAccount;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balance;

}