package bikram.businessmanager.service;

import bikram.businessmanager.dto.BalanceSheetRow;
import bikram.businessmanager.dto.JournalLineDto;
import bikram.businessmanager.dto.ProfitLossReport;
import bikram.businessmanager.dto.ProfitandLossDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.entity.account.JournalLine;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.entity.account.VoucherType;
import bikram.businessmanager.entity.inventory.DocumentType;
import bikram.businessmanager.entity.inventory.SystemAccount;
import bikram.businessmanager.repository.JournalEntryRepository;
import bikram.businessmanager.utils.BillNumberGenerator;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JournalEntryService extends BaseService {

    private JournalEntryRepository repository;
    private  AccountingService accountingService;
    private  SubAccountService subAccountService;
    private CompanyService companyService;
    public JournalEntryService(JournalEntryRepository repository,
                               AccountingService accountingService,
                               SubAccountService subAccountService,
                               CompanyService companyService) {
        super(repository);
        this.repository=repository;
        this.subAccountService=subAccountService;
        this.accountingService = accountingService;
        this.companyService = companyService;
    }

    public void postJournalEntry(EntityManager em,JournalEntry entry,Long companyId) {
        try {
            accountingService.validate(entry);
            String voucherNo = BillNumberGenerator.generateNumber(em,companyId,DocumentType.JOURNAL_ENTRY);
            entry.setVoucherNo(voucherNo);
            repository.postJournalEntry(em,entry);
        } catch (Exception e){
            e.printStackTrace(); // shows real DB error
            throw new RuntimeException("Failed to post Journal Entry", e);
        }

    }

    public void postJournalEntrywithoutem(JournalEntry entry){
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            postJournalEntry(em,entry,entry.getCompany().getId());
            tx.commit();
        } catch (Exception e){
            tx.rollback();
            e.printStackTrace(); // shows real DB error
            throw new RuntimeException("Failed to post Journal Entry", e);
        } finally {
            em.close();
        }
    }

    public List<JournalLineDto> getLedgerByCompany(Company company, SubAccount subAccount, LocalDate from,LocalDate to) {
        EntityManager em = JPAUtil.getEmf().createEntityManager();
        try {
            return repository.getLedger(em,company, subAccount, from, to);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

    void createDamageJournal( EntityManager em,Long companyId, BigDecimal total) {

        SubAccount expense = subAccountService.getSubAccountBycompanyandSysAcc(companyId,SystemAccount.DAMAGE_EXPENSE);
        SubAccount inventory = subAccountService.getSubAccountBycompanyandSysAcc(companyId, SystemAccount.INVENTORY);
        Company company = companyService.getById(companyId);
        JournalLine debit = JournalLine.builder()
                .company(company)
                .subAccount(expense)
                .debit(total)
                .credit(BigDecimal.ZERO)
                .build();

        JournalLine credit = JournalLine.builder()
                .company(company)
                .subAccount(inventory)
                .debit(BigDecimal.ZERO)
                .credit(total)
                .build();

        postJournal(em,
                company,
                VoucherType.DAMAGE,
                "Inventory damaged",
                List.of(debit, credit));
    }


    void postJournal(
            EntityManager em,
            Company company,
            VoucherType type,
            String description,
            List<JournalLine> lines
    ) {

        BigDecimal totalDebit = lines.stream()
                .map(JournalLine::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lines.stream()
                .map(JournalLine::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalStateException("Journal not balanced");
        }

        JournalEntry entry = JournalEntry.builder()
                .company(company)
                .voucherType(type)
                .date(LocalDate.now())
                .voucherNo(BillNumberGenerator.generateNumber(em,company.getId(), DocumentType.JOURNAL_ENTRY))
                .description(description)
                .build();

        lines.forEach(l -> l.setJournalEntry(entry));

        entry.setLine(lines);

        repository.postJournalEntry(em,entry);
    }

    void createAdjustmentJournal(
            EntityManager em,
            Company company,
            BigDecimal difference,
            BigDecimal costPrice
    ) {

        BigDecimal amount = difference.abs().multiply(costPrice);

        SubAccount inventory = subAccountService.getSubAccountBycompanyandSysAcc(company.getId(), SystemAccount.INVENTORY);

        if (difference.compareTo(BigDecimal.ZERO) > 0) {

            SubAccount gain =
                    subAccountService.getSubAccountBycompanyandSysAcc(company.getId(), SystemAccount.INVENTORY_ADJUSTMENT_GAIN);

            postJournal(em,
                    company,
                    VoucherType.ADJUSTMENT,
                    "Inventory gain",
                    List.of(
                            JournalLine.builder()
                                    .company(company)
                                    .subAccount(inventory)
                                    .debit(amount)
                                    .credit(BigDecimal.ZERO)
                                    .build(),
                            JournalLine.builder()
                                    .company(company)
                                    .subAccount(gain)
                                    .debit(BigDecimal.ZERO)
                                    .credit(amount)
                                    .build()
                    ));

        } else {

            SubAccount loss =
                    subAccountService.getSubAccountBycompanyandSysAcc(company.getId(), SystemAccount.INVENTORY_ADJUSTMENT_LOSS);

            postJournal(
                    em,
                    company,
                    VoucherType.ADJUSTMENT,
                    "Inventory loss",
                    List.of(
                            JournalLine.builder()
                                    .company(company)
                                    .subAccount(loss)
                                    .debit(amount)
                                    .credit(BigDecimal.ZERO)
                                    .build(),
                            JournalLine.builder()
                                    .company(company)
                                    .subAccount(inventory)
                                    .debit(BigDecimal.ZERO)
                                    .credit(amount)
                                    .build()
                    ));
        }
    }

    public ProfitLossReport getProfitAndLoss(
            Long companyId,
            LocalDate start,
            LocalDate end) {

        var em = JPAUtil.getEmf().createEntityManager();

        try {

            List<ProfitandLossDto> rows =
                    repository.getProfitandLoss(em, companyId, start, end);

            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;

            for (ProfitandLossDto row : rows) {

                BigDecimal amount =
                        row.amount() == null ? BigDecimal.ZERO : row.amount();

                switch (row.accountType()) {

                    case REVENUE -> totalIncome = totalIncome.add(amount);

                    case EXPENSE -> totalExpense = totalExpense.add(amount);
                }
            }

            BigDecimal netProfit = totalIncome.subtract(totalExpense);

            return new ProfitLossReport(
                    rows,
                    totalIncome,
                    totalExpense,
                    netProfit
            );

        } finally {
            em.close();
        }
    }
}
