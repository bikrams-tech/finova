package bikram.businessmanager.entity.inventory;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Customer;
import bikram.businessmanager.entity.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    // 🔹 Company (VERY IMPORTANT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 🔹 Customer (Better than raw ID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime salesDate;

    @Column(nullable = false)
    private BigDecimal subTotal;

    private BigDecimal vatAmount;

    private BigDecimal discount;

    private BigDecimal tax;

    @Column(nullable = false)
    private BigDecimal grandTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private String createdBy;

    private String fiscalYear;

    // 🔹 Sale Items (One Sale → Many Products)
    @OneToMany(mappedBy = "sales",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SaleItem> salesItems = new ArrayList<>();
}