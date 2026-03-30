package bikram.businessmanager.repository;

import bikram.businessmanager.dto.TrailBalanceRow;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TrailBalanceRepository {

    public List<TrailBalanceRow> getTrailBalanceRow(EntityManager em, Long companyId) {

        String jpql = """
        SELECT new bikram.businessmanager.dto.TrailBalanceRow(
        t.journalEntry.date,
            t.subAccount.name,
            t.journalEntry.voucherNo,
            SUM(COALESCE(t.debit,0)),
            SUM(COALESCE(t.credit,0))
        )
        FROM JournalLine t
        WHERE t.company.id = :companyId
        GROUP BY t.subAccount.name
        ORDER BY t.subAccount.name
        """;

        return em.createQuery(jpql, TrailBalanceRow.class)
                .setParameter("companyId", companyId)
                .getResultList();
    }
}
