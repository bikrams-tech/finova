package bikram.businessmanager.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VatReport {
    private  Long id;
    private  LocalDate fiscalYear;
    private  LocalDate month;
    private  Double totalSales;
    private  Double totalPurchase;
    private  Double outPutVat;
    private  Double inPutVat;
    private  Double vatPayable;
    private  LocalDateTime genertedDate;

    public  VatReport(){}
}
