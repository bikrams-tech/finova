package bikram.businessmanager.entity.account;

import bikram.businessmanager.entity.Company;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts",
uniqueConstraints  ={
        @UniqueConstraint(columnNames = {"company_id","code"}),
        @UniqueConstraint(columnNames = {"company_id","name"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements AccountNode{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private AccountGroup accountGroup;

    @Column(nullable = false)
    private String code;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    private List<SubAccount> subAccounts = new ArrayList<>();

    private boolean systemGenerated;

    @Override
    public String getDisplayName() {
        return name;
    }
}
