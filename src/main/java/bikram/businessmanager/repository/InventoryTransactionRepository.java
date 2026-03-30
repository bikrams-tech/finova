package bikram.businessmanager.repository;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.inventory.InventoryTransaction;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class InventoryTransactionRepository extends BaseRepository<InventoryTransaction> {

    public InventoryTransactionRepository() {
        super(InventoryTransaction.class);
    }

    public List<InventoryTransaction> getStockTransactionsByCompanyAndProduct(EntityManager em,Long companyId, Long productId) {
            return em.createQuery(
                            """
                            SELECT st
                            FROM StockTransaction st
                            WHERE st.product.id = :productId
                            AND st.company.id = :companyId
                            ORDER BY st.date ASC
                            """,
                            InventoryTransaction.class
                    )
                    .setParameter("productId", productId)
                    .setParameter("companyId", companyId)
                    .getResultList();
    }

    public List<InventoryTransaction> findPageByCompany(
            EntityManager em,
            Long companyId,
            int limit,
            int offset
    ) {
        return em.createQuery(
                        "SELECT i FROM InventoryTransaction i WHERE i.company.id = :companyId ORDER BY i.date DESC",
                        InventoryTransaction.class
                )
                .setParameter("companyId", companyId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}