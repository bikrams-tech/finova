package bikram.businessmanager.repository;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AccountRepository extends BaseRepository<Account>{
    public AccountRepository() {
        super(Account.class);
    }

    public List<Account> getAllAccountWithSubacByCompany(EntityManager em,Long companyId) {
            return em.createQuery(
                            """
                            SELECT DISTINCT a
                            FROM Account a
                            LEFT JOIN FETCH a.subAccounts
                            WHERE a.company.id = :companyId
                            """,
                            Account.class
                    )
                    .setParameter("companyId", companyId)
                    .getResultList();


    }

    public Account findByCode(EntityManager em,String code) {
            return em.createQuery(
                    "SELECT e FROM Account e WHERE e.code = :code", Account.class
            )
                    .setParameter("code",code)
                    .getResultList()
                    .getFirst();
    }
}
