package bikram.businessmanager.service;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.entity.account.JournalLine;
import bikram.businessmanager.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;

public class AccountingService {

    public void validate(JournalEntry entry) {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (JournalLine line : entry.getLine()) {
            totalDebit = totalDebit.add(line.getDebit());
            totalCredit = totalCredit.add(line.getCredit());
        }

        if (totalDebit.compareTo(totalCredit) != 0 ){
            throw new IllegalStateException("Unbalanced journal entry");
        }
    }
}
