package bikram.businessmanager.service;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.PaymentMethod;
import bikram.businessmanager.entity.PaymentStatus;
import bikram.businessmanager.entity.Supplier;
import bikram.businessmanager.entity.account.JournalLine;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.entity.account.VoucherType;
import bikram.businessmanager.entity.inventory.*;
import bikram.businessmanager.repository.PurchaseRepository;
import bikram.businessmanager.utils.BillNumberGenerator;

import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseService extends BaseService<Purchase> {

    private final PurchaseRepository purchaseRepository;
    private final InventoryService inventoryService;
    private final InventorytranstionService inventorytranstionService;
    private final JournalEntryService journalEntryService;
    private final SupplierService supplierService;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           InventoryService inventoryService,
                           InventorytranstionService inventorytranstionService,
                           JournalEntryService journalEntryService,
                           SupplierService supplierService) {

        super(purchaseRepository);

        this.purchaseRepository = purchaseRepository;
        this.inventoryService = inventoryService;
        this.inventorytranstionService = inventorytranstionService;
        this.journalEntryService = journalEntryService;
        this.supplierService = supplierService;
    }

    public void purchase(
            Company company,
            Product product,
            BigDecimal qty,
            Supplier supplier,
            PaymentMethod paymentMethod
    ) {
        var em = JPAUtil.getEmf().createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            if (supplier == null) {
                supplier = supplierService.getUnknownSupplier(em, company.getId());
            }

            validateQty(qty);

            BigDecimal total = product.getCostPrice().multiply(qty);

            // 1 Update stock
            inventoryService.increaseStock(em, company, product, qty);

            // 2 Inventory movement
            inventorytranstionService.saveInventoryTransaction(
                    em, company, product, qty, TransactionType.PURCHASE
            );

            // 3 Accounting
            createPurchaseJournal(em, company, total, paymentMethod);

            // 4 Purchase document
            createPurchaseDocument(em, company, product, qty, supplier, paymentMethod, total);
            tx.commit();
        } catch (Exception e){
            tx.rollback();
            e.printStackTrace();
            throw e;
        }
        finally {
            em.close();
        }
    }

    private void validateQty(BigDecimal qty) {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    private void createPurchaseJournal(EntityManager em,
            Company company,
            BigDecimal total,
            PaymentMethod paymentMethod
    ) {

        SubAccount inventory =
                inventoryService.getAccount(company.getId(), SystemAccount.INVENTORY);
        Long companyid = company.getId();
        SubAccount creditAccount =
                paymentMethod == PaymentMethod.CASH
                        ? inventoryService.getAccount(companyid, SystemAccount.CASH)
                        : inventoryService.getAccount(companyid, SystemAccount.ACCOUNTS_PAYABLE);

        JournalLine debit = JournalLine.builder()
                .company(company)
                .subAccount(inventory)
                .debit(total)
                .credit(BigDecimal.ZERO)
                .build();

        JournalLine credit = JournalLine.builder()
                .company(company)
                .subAccount(creditAccount)
                .debit(BigDecimal.ZERO)
                .credit(total)
                .build();

        journalEntryService.postJournal(
                em,
                company,
                VoucherType.PURCHASE,
                "Stock purchase",
                List.of(debit, credit)
        );
    }

    public void purchaseReturn(
            EntityManager em,
            Company company,
            Product product,
            BigDecimal qty
    ) {

        validateQty(qty);

        inventoryService.decreaseStock(em, company, product, qty);

        inventorytranstionService.saveInventoryTransaction(
                em, company, product, qty, TransactionType.PURCHASE_RETURN
        );

        createPurchaseReturnJournal(em, company, product, qty);
    }

    private void createPurchaseDocument(
            EntityManager em,
            Company company,
            Product product,
            BigDecimal qty,
            Supplier supplier,
            PaymentMethod paymentMethod,
            BigDecimal total
    ) {

        Purchase purchase = new Purchase();

        purchase.setCompany(company);
        purchase.setSupplier(supplier);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setPaymentMethod(paymentMethod);
        purchase.setPaymentStatus(PaymentStatus.PAID);
        purchase.setInvoiceNumber(
                BillNumberGenerator.generateNumber(em,company.getId(),DocumentType.PURCHASE_INVOICE)
        );
        purchase.setTotalAmount(total);
        purchase.setGrandTotal(total);

        purchaseRepository.save(em, purchase);
    }

    private void createPurchaseReturnJournal(
            EntityManager em,
            Company company,
            Product product,
            BigDecimal qty
    ) {

        BigDecimal total = product.getCostPrice().multiply(qty);

        SubAccount payable =
                inventoryService.getAccount(company.getId(), SystemAccount.ACCOUNTS_PAYABLE);

        SubAccount inventory =
                inventoryService.getAccount(company.getId(), SystemAccount.INVENTORY);

        journalEntryService.postJournal(
                em,
                company,
                VoucherType.PURCHASE_RETURN,
                "Purchase return - " + product.getName(),
                List.of(
                        JournalLine.builder()
                                .company(company)
                                .subAccount(payable)
                                .debit(total)
                                .credit(BigDecimal.ZERO)
                                .build(),
                        JournalLine.builder()
                                .company(company)
                                .subAccount(inventory)
                                .debit(BigDecimal.ZERO)
                                .credit(total)
                                .build()
                ));
    }

}