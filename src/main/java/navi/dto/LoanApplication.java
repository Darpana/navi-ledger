package navi.dto;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Table(name = "loan_application")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "bank_id")
    private Integer bankId;

    @Column(name = "principal_amount")
    private Double principalAmount;

    @Column
    private Double roi;

    @Column
    private Integer tenure;

//    @Column(name = "disbursal_date")
//    private Date disbursalDate;

    @Column(name = "interest_amount")
    private Double interestAmount;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column
    private Double emi;
}
