package bikram.businessmanager.service;

import bikram.businessmanager.repository.*;

public class ServiceContainer {
    private final   AccountService  accountService = new AccountService(new AccountRepository());
    private final   AccountingService accountingService = new AccountingService();
    private final CompanyService companyService = new CompanyService(new CompanyRepository());
    private final CustomerService customerService = new CustomerService(new CustomerRepository());
    private final EmployeeService employeeService = new EmployeeService();

    private final InventorytranstionService inventorytranstionService = new InventorytranstionService(new InventoryTransactionRepository());
    private final ProductService productService = new ProductService(new ProductRepository());
    private final SupplierService supplierService = new SupplierService(new SuplierRepository(),companyService);
    private final SubAccountService subAccountService = new SubAccountService(new SubaccountRepository());
    private final JournalEntryService journalEntryService = new JournalEntryService(new JournalEntryRepository(),
            accountingService,
            subAccountService,
            companyService);
    private final InventoryService inventoryService = new InventoryService(
            new InventoryRepository(),
            new InventoryTransactionRepository(),
            journalEntryService,
            new SubaccountRepository(),
            new CompanyRepository()
    );
    private final PurchaseService purchaseService = new PurchaseService(new PurchaseRepository(),
            inventoryService,
            inventorytranstionService,
            journalEntryService,
            supplierService
    );
    private final SalesService salesService = new SalesService(new SalesRepository(),
            inventoryService,
            inventorytranstionService,
            journalEntryService,
            customerService,
            companyService,
            productService);

    private final ContraEntryService contraEntryService = new ContraEntryService(
            new ContraVoucherRepository(),
            new JournalEntryRepository(),
            subAccountService,
            companyService
    );
    private final TrailBalanceService trailBalanceService = new TrailBalanceService(
            new TrailBalanceRepository()
    );

    public TrailBalanceService getTrailBalanceService() {
        return trailBalanceService;
    }

    public ContraEntryService getContraEntryService() {
        return contraEntryService;
    }

    public BalanceSheetService balanceSheetService = new BalanceSheetService(
            new BalanceSheetRepository()
    );

    public BalanceSheetService getBalanceSheetService() {
        return balanceSheetService;
    }

    public AccountingService getAccountingService() {
        return accountingService;
    }

    public AccountService getAccountService() {return accountService;}

    public CompanyService getCompanyService() {return companyService;}

    public CustomerService getCustomerService() {
        return customerService;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public InventorytranstionService getInventorytranstionService() {
        return inventorytranstionService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public PurchaseService getPurchaseService() {
        return purchaseService;
    }

    public SalesService getSalesService() {
        return salesService;
    }

    public SupplierService getSupplierService() {
        return supplierService;
    }

    public JournalEntryService getJournalEntryService() {
        return journalEntryService;
    }
    public SubAccountService getSubAccountService(){return subAccountService;}
}
