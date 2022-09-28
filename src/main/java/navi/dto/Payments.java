package navi.dto;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Table(name = "payments")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payments {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column
    private Double amount;

    @Column(name = "loan_application_id")
    private String loanApplicationId;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "payment_type")
    private String paymentType;
}
