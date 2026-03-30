package bikram.businessmanager.repository;

import bikram.businessmanager.dto.JournalLineDto;
import bikram.businessmanager.dto.ProfitandLossDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.*;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JournalEntryRepository extends BaseRepository<JournalEntry>{

    public JournalEntryRepository(){
        super(JournalEntry.class);
    }

    public void postJournalEntry(EntityManager em,JournalEntry entry) {
            entry.setStatus(JournalEntryStatus.POSTED);
            em.persist(entry);
    }
    @Override
    public List<JournalEntry> findAllByCompany(EntityManager em,Long companyId) {
            return em.createQuery(
                    "SELECT DISTINCT j FROM JournalEntry j\n" +
                            "LEFT JOIN FETCH j.line\n" +
                            "WHERE j.company.id = :companyId\n" +
                            "ORDER BY j.date DESC", JournalEntry.class
            )
                    .setParameter("companyId",companyId)
                    .getResultList();
    }

    public List<BalanceSheetRow> getBalanceSheet(EntityManager em,Long companyId){
            String jpql = """
        SELECT new bikram.businessmanager.dto.BalanceSheetRow(
               g.name,
               sa.name,
               SUM(jl.debit),
               SUM(jl.credit)
        )
        FROM JournalLine jl
        JOIN jl.subAccount sa
        JOIN sa.account a
        JOIN a.group g
        JOIN jl.journalEntry je
        WHERE je.company.id = :companyId
        GROUP BY g.name, sa.name
        """;

            return em.createQuery(jpql, BalanceSheetRow.class)
                    .setParameter("companyId", companyId)
                    .getResultList();
    }

    public List<JournalLineDto> getLedger(
            EntityManager em,
            Company company,
            SubAccount subAccount,
            LocalDate from,
            LocalDate to
    ) {
            List<JournalLine> lines = em.createQuery(
                            """
                            SELECT jl
                            FROM JournalLine jl
                            JOIN FETCH jl.journalEntry je
                            WHERE jl.subAccount = :subAccount
                            AND je.company = :company
                            AND je.date BETWEEN :from AND :to
                            ORDER BY je.date ASC, jl.id ASC
                            """,
                            JournalLine.class
                    ).setParameter("subAccount", subAccount)
                    .setParameter("company", company)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();


            List<JournalLineDto> result = new ArrayList<>();

            BigDecimal runningBalance = BigDecimal.ZERO;

            for (JournalLine line : lines) {

                BigDecimal debit =
                        line.getDebit() == null
                                ? BigDecimal.ZERO
                                : line.getDebit();

                BigDecimal credit =
                        line.getCredit() == null
                                ? BigDecimal.ZERO
                                : line.getCredit();

                runningBalance =
                        runningBalance.add(debit)
                                .subtract(credit);

                JournalLineDto dto =
                        JournalLineDto.builder()
                                .date(line.getJournalEntry().getDate())
                                .voucherNo(line.getJournalEntry().getVoucherNo())
                                .description(line.getNote())
                                .debit(debit)
                                .credit(credit)
                                .balance(runningBalance)
                                .build();

                result.add(dto);
            }

            return result;
    }

    public List<ProfitandLossDto> getProfitandLoss(
            EntityManager em,
            Long companyId,
            LocalDate start,
            LocalDate end) {

        return em.createQuery(
                        """
                        SELECT new bikram.businessmanager.dto.ProfitandLossDto(
                            l.subAccount.id,
                            l.subAccount.name,
                            l.subAccount.account.accountType,
                            SUM(
                                CASE
                                    WHEN l.subAccount.account.accountType = bikram.businessmanager.entity.account.AccountType.EXPENSE
                                        THEN COALESCE(l.debit,0) - COALESCE(l.credit,0)
                                    ELSE
                                        COALESCE(l.credit,0) - COALESCE(l.debit,0)
                                END
                            )
                        )
                        FROM JournalLine l
                        WHERE l.subAccount.account.accountType IN :types
                          AND l.company.id = :companyId
                          AND l.journalEntry.date BETWEEN :start AND :end
                        GROUP BY l.subAccount.id,
                                 l.subAccount.name,
                                 l.subAccount.account.accountType
                        ORDER BY l.subAccount.name
                        """,
                        ProfitandLossDto.class
                )
                .setParameter("companyId", companyId)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("types", List.of(
                        bikram.businessmanager.entity.account.AccountType.REVENUE,
                        bikram.businessmanager.entity.account.AccountType.EXPENSE
                ))
                .getResultList();
    }

}
