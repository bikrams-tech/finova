package bikram.businessmanager.entity.account;

import bikram.businessmanager.entity.inventory.SystemAccount;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sub_accounts",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_id", "code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubAccount implements AccountNode{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private AccountCategory accountCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_account")
    private SystemAccount systemAccount;

    @Override
    public String getDisplayName() {
        return "   └── " + name;
    }
}
