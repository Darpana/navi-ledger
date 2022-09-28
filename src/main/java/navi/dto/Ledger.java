package navi.dto;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ledger")
@Entity
public class Ledger {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "loan_application_id")
    private String loanApplicationId;

    @Column
    private Double amount;

    @Column(name = "outstanding_balance")
    private Double outstandingBalance;

    @Column(name = "txn_no")
    private Integer txnNo;

    @Column(name = "paid_emi_count")
    private Integer paidEmiCount;

    @Column(name = "emis_left")
    private Integer emisLeft;

    @Transient
    private boolean redundant;
}
