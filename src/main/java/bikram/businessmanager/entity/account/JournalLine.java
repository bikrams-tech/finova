package bikram.businessmanager.entity.account;

import bikram.businessmanager.entity.Company;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "journallines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "sub_account_id")
    private SubAccount subAccount;

    private BigDecimal debit;
    private BigDecimal credit;
    private String note;

    @ManyToOne
    @JoinColumn(name = "journal_entry_id")
    private  JournalEntry journalEntry;

    public JournalLine(Company company,
                       SubAccount subAccount,
                       BigDecimal debit,
                       BigDecimal credit,
                       String note) {

        if (company == null)
            throw new IllegalArgumentException("Company is required");

        if (subAccount == null)
            throw new IllegalArgumentException("Account is required");

        if (debit == null) debit = BigDecimal.ZERO;
        if (credit == null) credit = BigDecimal.ZERO;

        if (debit.compareTo(BigDecimal.ZERO) < 0 ||
                credit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Negative amounts not allowed");
        }

        if (debit.compareTo(BigDecimal.ZERO) > 0 &&
                credit.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Cannot have both debit and credit");
        }

        if (debit.compareTo(BigDecimal.ZERO) == 0 &&
                credit.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Either debit or credit required");
        }

        this.company = company;
        this.subAccount = subAccount;

        this.debit = debit;
        this.credit = credit;
    }


}
