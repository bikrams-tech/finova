package bikram.businessmanager.repository;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.inventory.Inventory;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class InventoryRepository extends BaseRepository<Inventory>{
    public InventoryRepository(){
        super(Inventory.class);
    }

    public Inventory findByProductAndCompany(EntityManager em,Long companyId, Long productId) {
            return em.createQuery(
                            "SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.company.id = :companyId",
                            Inventory.class
                    )
                    .setParameter("productId", productId)
                    .setParameter("companyId", companyId)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();


    }

    public Optional<Inventory> findoptionalByProductAndCompany(EntityManager em,Long productId, Long companyId) {
            List<Inventory> result = em.createQuery(
                            "SELECT i FROM Inventory i WHERE i.product.Id = :productId AND i.company.id = :companyId",
                            Inventory.class
                    )
                    .setParameter("productId", productId)
                    .setParameter("companyId", companyId)
                    .getResultList();

            return result.stream().findFirst();
    }

    public List<Inventory> search(EntityManager em,String keyword) {


            if (keyword == null || keyword.isBlank()) {
                return em.createQuery(
                        "SELECT i FROM Inventory i", Inventory.class
                ).getResultList();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return em.createQuery(
                            "SELECT i FROM Inventory i " +
                                    "JOIN i.product p " +
                                    "JOIN i.company c " +
                                    "WHERE LOWER(p.name) LIKE :pattern " +
                                    "OR LOWER(p.barcode) LIKE :pattern " +
                                    "OR LOWER(c.name) LIKE :pattern",
                            Inventory.class
                    )
                    .setParameter("pattern", pattern)
                    .getResultList();
    }

    public BigDecimal getStockByProductAndCompany(EntityManager em,Long productId, Long companyId) {
        try {
            return em.createQuery(
                            "SELECT COALESCE(i.quantity,0) FROM Inventory i WHERE i.product.id = :productId AND i.company.id = :companyId",
                            BigDecimal.class
                    )
                    .setParameter("productId", productId)
                    .setParameter("companyId", companyId)
                    .getSingleResult();
        } catch (NoResultException e){
            return BigDecimal.ZERO;
        }
    }

    public void updateWithEntity(EntityManager entityManager,Inventory inventory) {
        entityManager.merge(inventory);
    }
}
