package bikram.businessmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true,nullable = true)
    private String panNumber;

    @Column(unique = true ,nullable = true)
    private String vatNumber;

    @Column(unique = true,nullable = true)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType; // Pvt Ltd, Partnership, Sole Proprietor

    private String ownerName;

    @Enumerated(EnumType.STRING)
    private IndustryType industryType;

    private String address;
    private String phone;
    private String email;

    private LocalDate fiscalYearStart;
    private LocalDate fiscalYearEnd;

    private boolean active;

    private Long branchId;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return this.companyName;   // or getName()
    }
}