package bikram.businessmanager.entity.inventory;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "sales_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sales_id", nullable = false)
    private Sale sales;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal discount;

    private BigDecimal tax;

    private BigDecimal totalPrice;

    @PrePersist
    @PreUpdate
    public void calculateAmounts() {

        if (quantity == null) quantity = BigDecimal.ZERO;
        if (unitPrice == null) unitPrice = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        if (tax == null) tax = BigDecimal.ZERO;

        BigDecimal subtotal = quantity.multiply(unitPrice);

        totalPrice = subtotal
                .add(tax)
                .subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);
    }
}