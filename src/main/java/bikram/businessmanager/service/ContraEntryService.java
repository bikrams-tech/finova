package bikram.businessmanager.service;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.ContraVoucher;
import bikram.businessmanager.entity.account.*;
import bikram.businessmanager.entity.inventory.DocumentType;
import bikram.businessmanager.repository.ContraVoucherRepository;
import bikram.businessmanager.repository.JournalEntryRepository;
import bikram.businessmanager.utils.BillNumberGenerator;
import bikram.businessmanager.utils.JPAUtil;
import bikram.businessmanager.utils.SessionContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ContraEntryService  extends BaseService<ContraVoucher> {
    ContraVoucherRepository contraVoucherRepository;
    JournalEntryRepository journalEntryRepository;
    SubAccountService subAccountService;
    CompanyService companyService;
    public ContraEntryService(
            ContraVoucherRepository contraVoucherRepository,
            JournalEntryRepository journalEntryRepository,
                              SubAccountService subAccountService,
                              CompanyService companyService){
        super(contraVoucherRepository);
        this.contraVoucherRepository = contraVoucherRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.subAccountService = subAccountService;
        this.companyService = companyService;
    }
    public void postContraEntry(Long from, Long to,BigDecimal amount){
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();
        if (from.equals(to)){
            throw new IllegalArgumentException("From and To account cannot be same");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        try {
            tx.begin();

            Company company = companyService.getById(SessionContext.getCurrentCompanyId());
            SubAccount fromAcc = subAccountService.getSubAccountByIdAndCompany(company.getId(), from);
            SubAccount toAcc = subAccountService.getSubAccountByIdAndCompany(company.getId(), to);
            JournalEntry journalEntry = new JournalEntry();
            JournalLine fromLine = new JournalLine();
            fromLine.setSubAccount(fromAcc);
            fromLine.setJournalEntry(journalEntry);
            fromLine.setCompany(company);
            fromLine.setCredit(amount);
            fromLine.setDebit(BigDecimal.ZERO);
            fromLine.setNote("Amount transfer from " + fromAcc.getDisplayName());

            JournalLine toLine = new JournalLine();
            toLine.setJournalEntry(journalEntry);
            toLine.setCompany(company);
            toLine.setSubAccount(toAcc);
            toLine.setDebit(amount);
            toLine.setCredit(BigDecimal.ZERO);
            toLine.setNote("Amount transfer from " + fromAcc.getDisplayName());

            journalEntry.setVoucherNo(BillNumberGenerator.generateNumber(em, company.getId(), DocumentType.CONTRA_ENTRY));
            journalEntry.setStatus(JournalEntryStatus.POSTED);
            journalEntry.setVoucherType(VoucherType.CONTRA);
            journalEntry.setDate(LocalDate.now());
            journalEntry.setCompany(company);
            journalEntry.setLine(List.of(fromLine, toLine));
            journalEntry.setDescription("amount transfor betwen " + fromAcc.getDisplayName() + " and " + toAcc.getDisplayName());

            journalEntryRepository.postJournalEntry(em,journalEntry);
            tx.commit();
        } catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }


    }


}
