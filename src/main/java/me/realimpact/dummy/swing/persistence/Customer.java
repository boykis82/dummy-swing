package me.realimpact.dummy.swing.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "zord_cust",
    indexes = @Index(name = "i_ci", columnList = "ci")
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

  @Builder
  public Customer(String ci) {
    this.ci = ci;
  }

}
