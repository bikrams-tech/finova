package bikram.businessmanager.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    private String firstName;
    private String lastName;
    private String citizenshipNo;
    private String phone;
    private String address;
    private String position;
    private Double salary;
    private LocalDateTime hireDate;
    private String status;
    @Timestamp
    private LocalDateTime createdat;
}
