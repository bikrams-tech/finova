/*package bikram.businessmanager.test;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.PaymentMethod;
import bikram.businessmanager.entity.inventory.Product;
import bikram.businessmanager.entity.inventory.Sale;
import bikram.businessmanager.entity.inventory.SaleItem;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.SaleWorkFolwService;
import bikram.businessmanager.utils.SessionContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleWorkflowTest {
    public static CompanyService companyService = new CompanyService();

    public static void main(String[] args) {


        SaleWorkFolwService workflow = new SaleWorkFolwService();

        Company company = (Company) companyService.getById(1L);

        Product product = new Product();
        product.setId(1L); // existing product in DB

        SaleItem item = new SaleItem();
        item.setProduct(product);
        item.setQuantity(new BigDecimal("2"));
        item.setPrice(new BigDecimal("100"));

        Sale sale = new Sale();
        sale.setCompany(company);
        sale.setSalesDate(LocalDateTime.now());
        sale.setInvoiceNumber("INV-001");
        sale.setGrandTotal(new BigDecimal(200));
        sale.setPaymentMethod(PaymentMethod.CASH);
        sale.setSubTotal(new BigDecimal(150));


        sale.getSalesItems().add(item);
        item.setSales(sale);

        workflow.processSale(company, sale);

        System.out.println("SALE TEST COMPLETED");
    }
}

 */