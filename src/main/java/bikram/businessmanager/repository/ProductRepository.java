package bikram.businessmanager.repository;

import bikram.businessmanager.dto.ProductDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProductRepository extends BaseRepository<Product>{
    public ProductRepository() {
        super(Product.class);
    }

    public List<ProductDto> getallDto(EntityManager em,Long companyId){
        return em.createQuery(
                """
                        SELECT new bikram.businessmanager.dto.ProductDto(
                        e.id,
                        e.name)
                        FROM Product e
                        WHERE e.company.id = :companyId
                        ORDER BY e.name 
                        """, ProductDto.class
        )
                .setParameter("companyId",companyId)
                .getResultList();
    }
}
