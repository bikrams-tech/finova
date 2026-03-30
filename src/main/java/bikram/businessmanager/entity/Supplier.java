package bikram.businessmanager.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String panNumber;
    private String vatNumber;
    private String address;
    private String phone;
    private String email;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    private Company company;
}
