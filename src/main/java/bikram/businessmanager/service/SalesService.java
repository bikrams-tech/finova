package bikram.businessmanager.service;

import bikram.businessmanager.dto.sale.SaleReportDto;
import bikram.businessmanager.dto.sale.SaleTableDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Customer;
import bikram.businessmanager.entity.PaymentMethod;
import bikram.businessmanager.entity.account.JournalLine;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.entity.account.VoucherType;
import bikram.businessmanager.entity.inventory.*;
import bikram.businessmanager.repository.*;
import bikram.businessmanager.utils.BillNumberGenerator;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SalesService extends BaseService<Sale> {

    private SalesRepository salesRepository;
    private InventoryService inventoryService;
    private InventorytranstionService inventorytranstionService;
    private JournalEntryService journalEntryService;
    private CustomerService customerService;
    private ProductService productService;
    private CompanyService companyService;

    public SalesService(
            SalesRepository salesRepository,
            InventoryService inventoryService,
            InventorytranstionService inventorytranstionService,
            JournalEntryService journalEntryService,
            CustomerService customerService,
            CompanyService companyService,
            ProductService productService
    ) {

        super(salesRepository);

        this.salesRepository = salesRepository;
        this.inventoryService = inventoryService;
        this.inventorytranstionService = inventorytranstionService;
        this.journalEntryService = journalEntryService;
        this.customerService = customerService;
        this.companyService = companyService;
        this.productService = productService;
    }


    private Sale createSaleDocument(
            EntityManager em,
            Company company,
            Product product,
            BigDecimal qty,
            Customer customer,
            PaymentMethod paymentMethod,
            BigDecimal total
    ) {

        Sale sale = new Sale();
        if (customer == null) {
            customer = customerService.getUnknownCostumerByCompany(em,company.getId());
        }

        sale.setCompany(company);
        sale.setCustomer(customer);
        sale.setSalesDate(LocalDateTime.now());
        sale.setPaymentMethod(paymentMethod);
        sale.setInvoiceNumber(
                BillNumberGenerator.generateNumber(em,company.getId(), DocumentType.SALES_INVOICE)
        );
        sale.setSubTotal(total);
        sale.setGrandTotal(total);

        salesRepository.save(em, sale);

        return sale;
    }


    public void sale(
            Company company,
            Product product,
            BigDecimal qty,
            Customer customer,
            PaymentMethod paymentMethod
    ) {

        EntityManager em = JPAUtil.getEmf().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            if (customer == null) {
                customer = customerService.getUnknownCostumerByCompany(em,company.getId());
            }

            BigDecimal stock =
                    inventoryService.getStock(em,company.getId(), product.getId());

            if (stock.compareTo(qty) < 0) {
                throw new RuntimeException("Not enough stock");
            }
            validateQty(qty);

            BigDecimal total =
                    product.getSellingPrice().multiply(qty);

            BigDecimal costAmount =
                    product.getCostPrice().multiply(qty);

            // 1 Sale Document
            Sale sale = createSaleDocument(
                    em, company, product, qty, customer, paymentMethod, total
            );

            // 2 Inventory
            inventoryService.decreaseStock(em, company, product, qty);

            // 3 Inventory Movement
            inventorytranstionService.saveInventoryTransaction(
                    em, company, product, qty, TransactionType.SALE
            );

            // 4 Accounting
            createSaleJournal(
                    em, company, total, costAmount, paymentMethod
            );

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw e;

        } finally {
            em.close();
        }
    }

    private void createSaleJournal(
            EntityManager em,
            Company company,
            BigDecimal sellingAmount,
            BigDecimal costAmount,
            PaymentMethod paymentMethod
    ) {
        SubAccount debitAccount =
                paymentMethod == PaymentMethod.CASH
                        ? inventoryService.getAccount(company.getId(), SystemAccount.CASH)
                        : inventoryService.getAccount(company.getId(), SystemAccount.ACCOUNTS_RECEIVABLE);

        SubAccount revenue = inventoryService.getAccount(company.getId(), SystemAccount.SALES_REVENUE);
        SubAccount cogs = inventoryService.getAccount(company.getId(), SystemAccount.COGS);
        SubAccount inventory = inventoryService.getAccount(company.getId(), SystemAccount.INVENTORY);

        JournalLine debitCash = JournalLine.builder()
                .company(company)
                .subAccount(debitAccount)
                .debit(sellingAmount)
                .credit(BigDecimal.ZERO)
                .build();

        JournalLine creditRevenue = JournalLine.builder()
                .company(company)
                .subAccount(revenue)
                .debit(BigDecimal.ZERO)
                .credit(sellingAmount)
                .build();

        JournalLine debitCogs = JournalLine.builder()
                .company(company)
                .subAccount(cogs)
                .debit(costAmount)
                .credit(BigDecimal.ZERO)
                .build();

        JournalLine creditInventory = JournalLine.builder()
                .company(company)
                .subAccount(inventory)
                .debit(BigDecimal.ZERO)
                .credit(costAmount)
                .build();

        journalEntryService.postJournal(
                em,
                company,
                VoucherType.SALES,
                "Product sale",
                List.of(debitCash, creditRevenue, debitCogs, creditInventory));
    }

    @Transactional
    public void saleReturn(
            EntityManager em,
            Company company,
            Product product,
            BigDecimal qty
    ) {
        validateQty(qty);

        inventoryService.increaseStock(em,company, product, qty);

        inventorytranstionService.saveInventoryTransaction(em,company, product, qty, TransactionType.SALE_RETURN);

        // reverse journal
        createSalesReturnJournal(em,company,product,qty);
    }

    private void validateQty(BigDecimal qty) {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    private void createSalesReturnJournal(EntityManager em,
            Company company,
            Product product,
            BigDecimal qty
    ) {

        BigDecimal sellingAmount =
                product.getSellingPrice().multiply(qty);

        BigDecimal costAmount =
                product.getCostPrice().multiply(qty);

        SubAccount receivable =
                inventoryService.getAccount(company.getId(), SystemAccount.ACCOUNTS_RECEIVABLE);

        SubAccount revenue =
                inventoryService.getAccount(company.getId(), SystemAccount.SALES_REVENUE);

        SubAccount inventory =
                inventoryService.getAccount(company.getId(), SystemAccount.INVENTORY);

        SubAccount cogs =
                inventoryService.getAccount(company.getId(), SystemAccount.COGS);

        journalEntryService.postJournal(em,
                company,
                VoucherType.SALES_RETURN,
                "Sales return - " + product.getName(),
                List.of(
                        JournalLine.builder()
                                .company(company)
                                .subAccount(revenue)
                                .debit(sellingAmount)
                                .credit(BigDecimal.ZERO)
                                .build(),
                        JournalLine.builder()
                                .company(company)
                                .subAccount(receivable)
                                .debit(BigDecimal.ZERO)
                                .credit(sellingAmount)
                                .build(),
                        JournalLine.builder()
                                .company(company)
                                .subAccount(inventory)
                                .debit(costAmount)
                                .credit(BigDecimal.ZERO)
                                .build(),
                        JournalLine.builder()
                                .company(company)
                                .subAccount(cogs)
                                .debit(BigDecimal.ZERO)
                                .credit(costAmount)
                                .build()
                ));
    }


    public boolean multipalSale(Sale sale) {
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();

        try {
            tx.begin();

            // Ensure customer
            if (sale.getCustomer() == null) {
                Customer customer = customerService.getUnknownCostumerByCompany(
                        em, sale.getCompany().getId()
                );
                sale.setCustomer(customer);
            }

            // Generate invoice number
            sale.setInvoiceNumber(
                    BillNumberGenerator.generateNumber(
                            em, sale.getCompany().getId(), DocumentType.SALES_INVOICE
                    )
            );

            BigDecimal totalCost = BigDecimal.ZERO;
            BigDecimal grandTotal = BigDecimal.ZERO;

            // Process each item
            for (SaleItem item : sale.getSalesItems()) {
                Product product = item.getProduct();

                // Check stock
                BigDecimal stock = inventoryService.getStock(
                        em, sale.getCompany().getId(), product.getId()
                );

                if (stock.compareTo(item.getQuantity()) < 0) {
                    throw new RuntimeException("Not enough stock for " + product.getName());
                }

                // Calculate item totals
                item.calculateAmounts();

                // Accumulate totals
                totalCost = totalCost.add(product.getCostPrice().multiply(item.getQuantity()));
                grandTotal = grandTotal.add(item.getTotalPrice());

                // Update inventory
                inventoryService.decreaseStock(em, sale.getCompany(), product, item.getQuantity());

                // Save inventory transaction
                inventorytranstionService.saveInventoryTransaction(
                        em, sale.getCompany(), product, item.getQuantity(), TransactionType.SALE
                );
            }

            // Set totals on sale
            sale.setSubTotal(grandTotal);
            sale.setGrandTotal(grandTotal);

            // Save sale
            salesRepository.save(em, sale);

            // Create journal entry once
            createSaleJournal(em, sale.getCompany(), grandTotal, totalCost, sale.getPaymentMethod());

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw e;

        } finally {
            em.close();
        }
    }

    public List<SaleTableDto> getSaleTableDto(Long companyId){
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return salesRepository.getSaleTableDto(em,companyId);
        } finally {
            em.close();
        }
    }

    public List<SaleReportDto> getSaleReportByProduct(Long companyid){
        var em = JPAUtil.getEmf().createEntityManager();

        try {
            return salesRepository.getSaleReportByProduct(em, companyid);
        }
        finally {
            em.close();
        }
    }
}
