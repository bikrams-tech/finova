package bikram.businessmanager.repository;

import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class VoucherRepository extends BaseRepository<JournalEntry> {

    public VoucherRepository() {
        super(JournalEntry.class);
    }

    public List<JournalEntry> findByCustomer(EntityManager em,Long customerId) {
        try {
            return em.createQuery(
                            "SELECT v FROM Voucher v WHERE v.customer.id = :custId",
                            JournalEntry.class)
                    .setParameter("custId", customerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Double getTotalDebitByAccount(EntityManager em,Long accountId, Long companyId) {
            Double result = em.createQuery(
                            "SELECT COALESCE(SUM(vl.debit),0) " +
                                    "FROM VoucherLine vl " +
                                    "WHERE vl.account.id = :accId " +
                                    "AND vl.voucher.company.id = :cid",
                            Double.class)
                    .setParameter("accId", accountId)
                    .setParameter("cid", companyId)
                    .getSingleResult();
            return result;
    }
}