package bikram.businessmanager.service;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Supplier;
import bikram.businessmanager.repository.SuplierRepository;

import jakarta.persistence.EntityManager;

public class SupplierService extends BaseService<Supplier> {
    private CompanyService companyService;
    private SuplierRepository repository;

    public SupplierService(SuplierRepository repository,
                           CompanyService companyService) {
        super(repository);
        this.repository = repository;
        this.companyService = companyService;
    }


    public Supplier getUnknownSupplier(EntityManager em,Long companyId) {
            Supplier supplier = repository.findUnknownByCompany(em, companyId);

            if (supplier == null) {
                Company company = companyService.getById(companyId);

                supplier = new Supplier();
                supplier.setCompany(company);
                supplier.setName("Unknown Supplier");
                supplier.setEmail("unknownsupplier@gmail.com");
                supplier.setAddress("Unknown Supplier");
                supplier.setPhone("0000000000");
                supplier.setVatNumber("0000000");

                repository.save(em, supplier);
            }

            return supplier;
        }
    }
