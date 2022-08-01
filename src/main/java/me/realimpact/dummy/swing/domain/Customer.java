package me.realimpact.dummy.swing.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(
    name = "zord_cust",
    indexes = @Index(name = "i_ci", columnList = "ci", unique = true)
)
public class Customer {
  @Id
  @Column(name = "svc_mgmt_num")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long custNum;

  @Version
  private Integer version;

  @Column(nullable = false)
  private String ci;
  
  @Column(nullable = false)
  private LocalDate birthDt;

  @Builder
  public Customer(String ci, LocalDate birthDt) {
    this.ci = ci;
    this.birthDt = birthDt;
  }

}
