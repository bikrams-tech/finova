package bikram.businessmanager.entity.account;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "journalentrys")
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(optional = true)
    @JoinColumn(name = "costumer_id")
    private Customer customer;
    @Column(unique = true)
    private String voucherNo;

    private LocalDate date;

    private String description;
    @Enumerated(EnumType.STRING)
    private VoucherType voucherType;

    @Enumerated(EnumType.STRING)
    private JournalEntryStatus status;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<JournalLine> line = new ArrayList<>();

}
