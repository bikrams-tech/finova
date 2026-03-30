package bikram.businessmanager.entity;

import bikram.businessmanager.entity.inventory.TransactionType;

import java.time.LocalDateTime;

public class Ledger {
    private Long id;
    private TransactionType transactionType;
    private Long referenceId;
    private Double debit;
    private Double credit;
    private LocalDateTime transactionDate;

    public Ledger(){}
}
