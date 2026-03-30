package bikram.businessmanager.repository;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class BalanceSheetRepository {

    public List<Object[]> getBalanceSheetRaw(EntityManager em, Long companyId, LocalDate endDate) {

        String jpql = """
            SELECT 
                sa.name,
                sa.account.accountType,
                SUM(
                    COALESCE(jl.debit, 0) - COALESCE(jl.credit, 0)
                )
            FROM JournalLine jl
            JOIN jl.subAccount sa
            JOIN jl.journalEntry je
            WHERE je.company.id = :companyId
              AND je.date <= :endDate
            GROUP BY sa.name, sa.account.accountType
            ORDER BY sa.account.accountType
        """;

        return em.createQuery(jpql, Object[].class)
                .setParameter("companyId", companyId)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}
