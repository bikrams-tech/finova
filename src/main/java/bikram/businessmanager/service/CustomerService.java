package bikram.businessmanager.service;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Customer;
import bikram.businessmanager.repository.CompanyRepository;
import bikram.businessmanager.repository.CustomerRepository;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CustomerService extends BaseService<Customer>{
    private CustomerRepository repository;
    private CompanyRepository companyRepository = new CompanyRepository();
    public CustomerService(CustomerRepository repository){
        super(repository);
        this.repository=repository;
    }

    public Customer getUnknownCostumerByCompany(EntityManager em,Long companyId){
        Customer customer = repository.findUnknownBycompany(em,companyId);
        Company company = companyRepository.findById(em,companyId);
        if (customer == null){
            customer = new Customer();
            customer.setCompany(company);
            customer.setAddress("Unknown");
            customer.setPhone("00000000");
            customer.setName("Unknown");
            customer.setPanNumber("000000");
            customer.setVatnumber("000000");
            return repository.save(em,customer);
        }
        return customer;
    }
}
