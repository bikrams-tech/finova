package bikram.businessmanager.entity.inventory;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Unit;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    private String name;
    private String barcode;
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private boolean vatApplicable;
    private BigDecimal vatRate;

    private BigDecimal taxRate;

    @Enumerated(EnumType.STRING)
    private Unit unit;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
