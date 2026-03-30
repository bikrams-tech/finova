package bikram.businessmanager.utils;

import bikram.businessmanager.entity.inventory.DocumentType;
import bikram.businessmanager.repository.InvoiceSequenceRepository;
import jakarta.persistence.EntityManager;

import java.time.Year;

public class BillNumberGenerator {

    private static final InvoiceSequenceRepository repository =
            new InvoiceSequenceRepository();

    public static String generateNumber(
            EntityManager em,
            Long companyId,
            DocumentType type
    ) {

        int year = Year.now().getValue();

        Long number =
                repository.getNextInvoiceNumber(em, companyId, year, type);

        return switch (type) {

            case PURCHASE_INVOICE ->
                    String.format("PI-%d-%05d", year, number);

            case SALES_INVOICE ->
                    String.format("SI-%d-%05d", year, number);

            case PAYMENT_VOUCHER ->
                    String.format("PV-%d-%05d", year, number);

            case RECEIPT_VOUCHER ->
                    String.format("RV-%d-%05d", year, number);

            case JOURNAL_ENTRY ->
                    String.format("JE-%d-%05d", year, number);
            case CONTRA_ENTRY ->
                String.format("CE-%d-%05d",year,number);
        };
    }
}