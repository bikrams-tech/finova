package bikram.businessmanager.service;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.repository.AccountRepository;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AccountService extends BaseService<Account>{
    private AccountRepository repository;
    public AccountService(AccountRepository accountRepository){
        super(accountRepository);
        this.repository=accountRepository;
    }


    public List<Account> getAllaccountandSubAccountBycompany(Long companyId) {
        EntityManager em = JPAUtil.getEmf().createEntityManager();
        try {
            return repository.getAllAccountWithSubacByCompany(em,companyId);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }
}
