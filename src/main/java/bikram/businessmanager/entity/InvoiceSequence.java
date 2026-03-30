package bikram.businessmanager.entity;

import bikram.businessmanager.entity.inventory.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "invoice_sequence",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"company_id", "year","documentType"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="company_id", nullable=false)
    private Long companyId;

    @Column(nullable=false)
    private int year;

    @Column(name="current_number", nullable=false)
    private Long currentNumber;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;
}