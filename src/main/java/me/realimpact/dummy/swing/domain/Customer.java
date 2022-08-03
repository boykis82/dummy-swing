package me.realimpact.dummy.swing.domain;

import lombok.*;
import javax.persistence.Id;
import javax.persistence.Version;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "zord_cust",
    indexes = @Index(name = "i_ci", columnList = "ci", unique = true)
)
public class Customer extends BaseEntity {
  @Id
  @Column(name = "cust_num")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long custNum;
  
  @Version
  private Integer version;
  
  @Column(nullable = false, name = "cust_nm")
  private String custNm;

  @Column(nullable = false, name = "ci")
  private String ci;
  
  @Column(nullable = false, name = "birth_dt")
  private LocalDate birthDt;

  @Builder
  private Customer(String ci, String custNm, LocalDate birthDt) {
    this.ci = ci;
    this.custNm = custNm;
    this.birthDt = birthDt;
  }

  public static Customer newCustomer(String ci, String custNm, LocalDate birthDt) {
    return Customer.builder()
        .ci(ci)
        .custNm(custNm)
        .birthDt(birthDt)
        .build();
  }
}
