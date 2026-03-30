package bikram.businessmanager.repository;

import bikram.businessmanager.dto.CompanyDto;
import bikram.businessmanager.dto.SubAccountDto;
import bikram.businessmanager.dto.SubAccountDtoWithAcount;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.entity.account.AccountCategory;
import bikram.businessmanager.entity.account.AccountType;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.entity.inventory.SystemAccount;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.math.BigDecimal;
import java.util.List;

public class SubaccountRepository extends BaseRepository<SubAccount>{

    public SubaccountRepository() {
        super(SubAccount.class);
    }

    public boolean existsByNameAndAccount(EntityManager em,String name, Account account) {
            Long count = em.createQuery(
                            "SELECT COUNT(s) FROM SubAccount s " +
                                    "WHERE LOWER(s.name) = LOWER(:name) " +
                                    "AND s.account = :account",
                            Long.class
                    )
                    .setParameter("name", name.trim())
                    .setParameter("account", account)
                    .getSingleResult();

            return count > 0;
    }

    public SubAccount findBySubAccountId(EntityManager em, Long companyId, Long id) {
            return em.createQuery(
                            "SELECT s FROM SubAccount s WHERE s.id = :id AND s.account.company.id = :companyId",
                            SubAccount.class)
                    .setParameter("id", id)
                    .setParameter("companyId", companyId)
                    .getSingleResult();
    }

    public SubAccount getSubAccountByCompanyAndCode(EntityManager em,Long companyId, String code) {
            return em.createQuery(
                            """
                            SELECT e FROM SubAccount e
                            WHERE e.account.company.id = :companyId
                            AND e.code = :code
                            """,
                            SubAccount.class
                    )
                    .setParameter("companyId", companyId)
                    .setParameter("code", code)
                    .getSingleResult();
    }

    public SubAccount findSubAccountByCompanyAndSystemAc(EntityManager em, Long companyId, SystemAccount type) {

        return em.createQuery(
                        "SELECT e FROM SubAccount e WHERE e.account.company.id = :companyId AND e.systemAccount = :type",
                        SubAccount.class
                )
                .setParameter("companyId", companyId)
                .setParameter("type", type)
                .getSingleResult();
    }

    public List<SubAccountDto> getAllSubAccountDtoBycompany(EntityManager em,Long companyId) {
        return em.createQuery(
                """
                        SELECT new bikram.businessmanager.dto.SubAccountDto(
                        e.id,
                        e.account.name,
                        e.name
                        )
                        From SubAccount e
                        WHERE e.account.company.id = :companyId
                        ORDER BY e.name
                        """,SubAccountDto.class
        )
                .setParameter("companyId",companyId)
                .getResultList();
    }

    public List<SubAccountDtoWithAcount> getAllDtowithaccount(EntityManager em,Long companyId){
        String jpql = """
                SELECT new bikram.businessmanager.dto.SubAccountDtoWithAcount(
                e.id,
                e.name,
                e.account.name,
                e.accountCategory,
                e.code)
                FROM SubAccount e
                WHERE e.account.company.id = :companyId
                ORDER BY e.name
                """;
        return em.createQuery(jpql,SubAccountDtoWithAcount.class)
                .setParameter("companyId",companyId)
                .getResultList();
    }


    @Override
    public List<SubAccount> findAllByCompany(EntityManager em,Long companyId) {
        return em.createQuery(
                "SELECT e FROM SubAccount e WHERE e.account.company.id = :companyId", SubAccount.class
        )
                .setParameter("companyId",companyId)
                .getResultList();
    }

    public BigDecimal getBalance(EntityManager em, Long companyId, Long accountId) {

        return em.createQuery(
                        """
                        SELECT COALESCE(SUM(j.debit),0) - COALESCE(SUM(j.credit),0)
                        FROM JournalLine j
                        WHERE j.company.id = :companyId
                          AND j.subAccount.id = :accountId
                        """,
                        BigDecimal.class
                )
                .setParameter("companyId", companyId)
                .setParameter("accountId", accountId)
                .getSingleResult();
    }
    public List<SubAccountDto> getAllContraAllowedAccountDto(EntityManager em,Long companyId){
        return em.createQuery(
                """
                        SELECT new bikram.businessmanager.dto.SubAccountDto(
                        e.id,
                        e.account.name,
                        e.name
                        )
                        From SubAccount e
                        WHERE e.account.company.id = :companyId
                        AND e.accountCategory IN (:types)
                        ORDER BY e.name
                        """,SubAccountDto.class
        )
                .setParameter("companyId",companyId)
                .setParameter("types", List.of(AccountCategory.BANK,AccountCategory.CASH))
                .getResultList();

}
}
