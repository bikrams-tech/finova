package bikram.businessmanager.service;

import bikram.businessmanager.dto.CompanyDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.entity.account.AccountCategory;
import bikram.businessmanager.entity.account.AccountType;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.entity.inventory.SystemAccount;
import bikram.businessmanager.repository.AccountRepository;
import bikram.businessmanager.repository.CompanyRepository;
import bikram.businessmanager.repository.SubaccountRepository;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class CompanyService extends BaseService<Company>{
    private CompanyRepository repository;
    private AccountRepository accountRepository = new AccountRepository();
    private SubaccountRepository subaccountRepository = new SubaccountRepository();
    private final Logger log = LoggerFactory.getLogger(CompanyService.class);
    public CompanyService(CompanyRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<Company> findAllByName(String name) {
        EntityManager em = JPAUtil.getEmf().createEntityManager();
        try {
            return repository.findAllByName(em,name);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }


    public List<CompanyDto> getallcompanyDto(){
        EntityManager em = JPAUtil.getEmf().createEntityManager();
        try {
            return repository.getAllCompanyDto(em);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }
    public List<CompanyDto> getAllCompanyDtoByName(String name) {
        EntityManager em = JPAUtil.getEmf().createEntityManager();
        try {
            return repository.findByNameallCompanyDto(em,name);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }


    @Override
    public Company create(Company company) {
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();

        try {
            tx.begin();

            repository.save(em, company);
            createSystemaccount(em, company);
            log.info("sucessfully create system account.");

            tx.commit();
            log.info("sucessfully create company.");
            return company;
        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("failed to create company",e);
            throw e;

        } finally {
            em.close();
        }
    }


    public void createSystemaccount(EntityManager em,Company company) {
        //main account create

        Account assets = createMainAccount(em,company,"assets", AccountType.ASSET,"1000");
        Account liabilities = createMainAccount(em,company,"liabilities",AccountType.LIABILITY,"2000");
        Account revenue = createMainAccount(em,company,"revenue",AccountType.REVENUE,"4000");
        Account expense = createMainAccount(em,company,"expense",AccountType.EXPENSE,"5000");


        //subaccount create

        createSubaccount(em,"cash",assets,"1000", SystemAccount.CASH,AccountCategory.CASH);
        createSubaccount(em,"Accounts Receivable",assets,"1100",SystemAccount.ACCOUNTS_RECEIVABLE,AccountCategory.RECEIVABLE);
        createSubaccount(em,"Inventory",assets,"1200",SystemAccount.INVENTORY,AccountCategory.INVENTORY);

        createSubaccount(em,"Accounts Payable",liabilities,"2000",SystemAccount.ACCOUNTS_PAYABLE,AccountCategory.PAYABLE);

        createSubaccount(em,"Sales Revenue",revenue,"4000",SystemAccount.SALES_REVENUE,AccountCategory.SALES);

        createSubaccount(em,"Cost of Goods Sold",expense,"5000",SystemAccount.COGS,AccountCategory.COGS);
        createSubaccount(em,"Damage Expense",expense,"6100",SystemAccount.DAMAGE_EXPENSE,AccountCategory.EXPENSE);
    }
    public Account createMainAccount(EntityManager em,Company company, String name, AccountType type,String code){
        Account account = Account.builder()
                .company(company)
                .name(name)
                .accountType(type)
                .code(code)
                .build();
        accountRepository.save(em,account);
        return account;
    }
    public SubAccount createSubaccount(EntityManager em, String name, Account account, String code, SystemAccount systemAccount, AccountCategory category) {
        SubAccount subAccount = SubAccount.builder()
                .name(name)
                .account(account)
                .code(code)
                .systemAccount(systemAccount)
                .accountCategory(category)
                .build();
        subaccountRepository.save(em,subAccount);
        return subAccount;
    }


}
