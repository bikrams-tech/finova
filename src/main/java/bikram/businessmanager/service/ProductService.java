package bikram.businessmanager.service;

import bikram.businessmanager.dto.ProductDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.repository.ProductRepository;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProductService extends BaseService<Product> {
    private ProductRepository repository;

    public ProductService(ProductRepository repository){
        super(repository);
        this.repository=repository;
    }

    public List<ProductDto> getallProductDto(Long companyId){
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return repository.getallDto(em,companyId);
        } finally {
            em.close();
        }
    }



}
