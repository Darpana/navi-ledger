package navi.dto;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Table(name = "bank")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="bank_name")
    private String bankName;
}
