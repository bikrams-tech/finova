package bikram.businessmanager.entity;

import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private ReferenceType referenceType; //Sale ,Purchase
    private Long referenceId;
    private Double amountPaid;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;
    private String status;

    public Payment(){}

}
