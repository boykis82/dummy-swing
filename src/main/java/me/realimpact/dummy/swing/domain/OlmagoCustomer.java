package me.realimpact.dummy.swing.domain;

import lombok.*;
import javax.persistence.Id;
import javax.persistence.Version;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Table(
    name = "zord_olmago_cust",
    indexes = @Index(name = "i_cust", columnList = "swing_cust_num", unique = true)
)
public class OlmagoCustomer extends BaseEntity {
  @Id
  @Column(name = "olmago_cust_id")
  private long olmagoCustId;

  @Version
  private Integer version;

  @OneToOne
  @JoinColumn(name = "swing_cust_num")
  private Customer swingCustomer;

  @Builder
  public OlmagoCustomer(long olmagoCustId, Customer swingCustomer) {
    this.olmagoCustId = olmagoCustId;
    this.swingCustomer = swingCustomer;
  }

}