package me.realimpact.dummy.swing.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "zord_svc")
public class MobilePhoneService {
  @Id
  @Column(name = "svc_mgmt_num")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long svcMgmtNum;

  @Version
  private Integer version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cust_num")
  private Customer customer;

  @Column(nullable = false)
  private String svcNum;

  @Column(nullable = false)
  private LocalDate svcScrbDt;

  private LocalDate svcTermDt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "fee_prod_id")
  private Product feeProduct;

  @Builder
  public MobilePhoneService(Customer customer,
                            String svcNum,
                            LocalDate svcScrbDt) {
    this.customer = customer;
    this.svcNum = svcNum;
    this.svcScrbDt = svcScrbDt;
  }


}
