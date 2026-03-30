package bikram.businessmanager.service;

import bikram.businessmanager.entity.*;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.entity.inventory.*;
import bikram.businessmanager.repository.*;

import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class InventoryService extends BaseService<Inventory> {

    private  InventoryRepository inventoryRepository;
    private InventoryTransactionRepository inventoryTransactionRepository;
    private JournalEntryService journalEntryService;
    private SubaccountRepository subaccountRepository;
    private CompanyRepository companyRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            InventoryTransactionRepository inventoryTransactionRepository,
            JournalEntryService journalEntryService,
            SubaccountRepository subaccountRepository,
            CompanyRepository companyRepository
    ) {

        super(inventoryRepository);

        this.inventoryRepository = inventoryRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.journalEntryService = journalEntryService;
        this.subaccountRepository = subaccountRepository;
        this.companyRepository = companyRepository;
    }

    /* =========================================================
       DAMAGE
       ========================================================= */

    public void damage(
            Long  companyId,
            Product product,
            BigDecimal qty
    ) {
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Company company = companyRepository.findById(em,companyId);
            validateQty(qty);

            decreaseStock(em, company, product, qty);

            InventoryTransaction transaction = new InventoryTransaction();

            transaction.setCompany(company);
            transaction.setProduct(product);
            transaction.setQuantity(qty);
            transaction.setType(TransactionType.DAMAGE);
            transaction.setDate(LocalDateTime.now());

            inventoryTransactionRepository.save(
                    em, transaction
            );

            journalEntryService.createDamageJournal(
                    em,
                    companyId,
                    product.getCostPrice().multiply(qty)
            );
            tx.commit();

        } catch (Exception e){
            tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

    /* =========================================================
       ADJUSTMENT
       ========================================================= */

    public void adjustment(
            Company company,
            Product product,
            BigDecimal newQty
    ) {
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Inventory inventory = getInventory(em, company, product);

            BigDecimal difference =
                    newQty.subtract(inventory.getQuantity());

            inventory.setQuantity(newQty);

            inventoryRepository.update(em, inventory);

            InventoryTransaction itx = new InventoryTransaction();
            itx.setCompany(company);
            itx.setQuantity(newQty);
            itx.setType(TransactionType.ADJUSTMENT);
            itx.setDate(LocalDateTime.now());
            itx.setProduct(product);
            itx.setReference("inventory adjustment");

            inventoryTransactionRepository.save(
                    em,
                    itx
            );

            journalEntryService.createAdjustmentJournal(
                    em,
                    company,
                    difference,
                    product.getCostPrice()
            );
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

    /* =========================================================
       STOCK MANAGEMENT
       ========================================================= */

    public void increaseStock(
            EntityManager em,
            Company company,
            Product product,
            BigDecimal qty
    ) {

        Inventory inventory = getInventory(em, company, product);

        inventory.setQuantity(
                inventory.getQuantity().add(qty)
        );

        inventoryRepository.updateWithEntity(em, inventory);
    }

    public void decreaseStock(
            EntityManager em,
            Company company,
            Product product,
            BigDecimal qty
    ) {

        Inventory inventory =
                getInventory(em, company, product);

        if (inventory.getQuantity().compareTo(qty) < 0) {
            throw new RuntimeException("Insufficient stock");
        }

        inventory.setQuantity(
                inventory.getQuantity().subtract(qty)
        );

        inventoryRepository.updateWithEntity(em, inventory);
    }

    /* =========================================================
       INVENTORY FETCH
       ========================================================= */

    private Inventory getInventory(
            EntityManager em,
            Company company,
            Product product
    ) {

        Optional<Inventory> optional =
                inventoryRepository.findoptionalByProductAndCompany(
                        em,
                        product.getId(),
                        company.getId()
                );

        if (optional.isPresent()) {
            return optional.get();
        }

        Inventory inventory = new Inventory();

        inventory.setCompany(company);
        inventory.setProduct(product);
        inventory.setQuantity(BigDecimal.ZERO);
        inventory.setDate(LocalDateTime.now());

        inventoryRepository.save(em, inventory);

        return inventory;
    }

    /* =========================================================
       VALIDATION
       ========================================================= */

    private void validateQty(BigDecimal qty) {

        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }
    }

    /* =========================================================
       SYSTEM ACCOUNT RESOLUTION
       ========================================================= */

    public SubAccount getAccount(
            Long companyId,
            SystemAccount type
    ) {
        var em = JPAUtil.getEmf().createEntityManager();
        return switch (type) {

            case CASH ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.CASH
                            );

            case ACCOUNTS_RECEIVABLE ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.ACCOUNTS_RECEIVABLE
                            );

            case ACCOUNTS_PAYABLE ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.ACCOUNTS_PAYABLE
                            );

            case INVENTORY ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.INVENTORY
                            );

            case SALES_REVENUE ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.SALES_REVENUE
                            );

            case COGS ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.COGS
                            );

            case DAMAGE_EXPENSE ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.DAMAGE_EXPENSE
                            );

            case INVENTORY_ADJUSTMENT_GAIN ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.INVENTORY_ADJUSTMENT_GAIN
                            );

            case INVENTORY_ADJUSTMENT_LOSS ->
                    subaccountRepository
                            .findSubAccountByCompanyAndSystemAc(
                                    em,
                                    companyId,
                                    SystemAccount.INVENTORY_ADJUSTMENT_LOSS
                            );
        };
    }

    /* =========================================================
       QUERIES
       ========================================================= */

    public List<Inventory> getAll(
            EntityManager em,
            Long companyId
    ) {

        return inventoryRepository.findAllByCompany(em, companyId);
    }

    public List<Inventory> search(
            String keyword
    ) {
        var em = JPAUtil.getEmf().createEntityManager();
        return inventoryRepository.search(em, keyword);
    }

    public BigDecimal getStock(
            EntityManager em,
            Long companyId,
            Long productId
    ) {
            BigDecimal stock = inventoryRepository.getStockByProductAndCompany(em, productId, companyId);
            if (stock == null){
                return BigDecimal.ZERO;
            }
            return stock;
    }

}