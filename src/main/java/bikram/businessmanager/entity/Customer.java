package bikram.businessmanager.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String panNumber;
    private String vatnumber;
    private String phone;
    private String address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Timestamp
    private LocalDateTime createdAt;
}
