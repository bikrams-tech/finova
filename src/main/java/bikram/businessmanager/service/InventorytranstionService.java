package bikram.businessmanager.service;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.inventory.InventoryTransaction;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.entity.inventory.TransactionType;
import bikram.businessmanager.repository.InventoryTransactionRepository;

import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InventorytranstionService extends BaseService<InventoryTransaction> {

    private final InventoryTransactionRepository inventoryTransactionRepository;

    public InventorytranstionService(
            InventoryTransactionRepository inventoryTransactionRepository
    ) {

        super(inventoryTransactionRepository);
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }

    /* =====================================================
       CREATE TRANSACTION
       ===================================================== */

    public void saveInventoryTransaction(
            EntityManager em,
            Company company,
            Product product,
            BigDecimal qty,
            TransactionType type
    ) {

        InventoryTransaction tx = new InventoryTransaction();

        tx.setCompany(company);
        tx.setProduct(product);
        tx.setQuantity(qty);
        tx.setType(type);
        tx.setDate(LocalDateTime.now());

        inventoryTransactionRepository.save(em, tx);
    }

    /* =====================================================
       GET TRANSACTIONS
       ===================================================== */

    public List<InventoryTransaction> getInventoryTrByAcandcompany(
            EntityManager em,
            Long companyId,
            Long productId
    ) {

        return inventoryTransactionRepository
                .getStockTransactionsByCompanyAndProduct(
                        em,
                        companyId,
                        productId
                );
    }

    public List<InventoryTransaction> getPageByCompany(Long currentCompanyId, int pageSize, int offset) {
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return inventoryTransactionRepository.findPageByCompany(
                    em,currentCompanyId,pageSize,offset
            );
        } finally {
            em.close();
        }
    }
}