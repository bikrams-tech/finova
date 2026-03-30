package bikram.businessmanager.entity;

import bikram.businessmanager.entity.account.SubAccount;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contra_vouchers")
public class ContraVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "from_acount")
    private SubAccount fromAcc;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_acount")
    private SubAccount toAcc;
    private String reference;
    private String narration;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
