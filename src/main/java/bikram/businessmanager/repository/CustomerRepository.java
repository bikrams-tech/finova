package bikram.businessmanager.repository;

import bikram.businessmanager.entity.Customer;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;


public class CustomerRepository extends BaseRepository<Customer>{
    public CustomerRepository(){
        super(Customer.class);
    }

    public Customer findUnknownBycompany(EntityManager em, Long companyid) {
        return em.createQuery(
                        "SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.name = 'Unknown'",
                        Customer.class
                )
                .setParameter("companyId", companyid)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
