package bikram.businessmanager.entity;

import java.time.LocalDateTime;

public class Expense {
    private Long id;
    private String title;
    private Double amount;
    private LocalDateTime expenseDate;
    private String cattegory;
    private PaymentMethod paymentMethod;
    private String note;
    private String createdBy;

    public Expense(){}
}
