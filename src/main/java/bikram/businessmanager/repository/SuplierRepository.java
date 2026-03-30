package bikram.businessmanager.repository;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Supplier;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class SuplierRepository extends BaseRepository<Supplier>{
    public SuplierRepository(){
        super(Supplier.class);
    }

    public Supplier findUnknownByCompany(EntityManager em,Long companyId) {

        return em.createQuery(
                        "SELECT s FROM Supplier s WHERE s.company.id = :companyId AND s.name = :name",
                        Supplier.class
                )
                .setParameter("companyId", companyId)
                .setParameter("name", "Unknown Supplier")
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
