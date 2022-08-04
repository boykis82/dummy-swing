package me.realimpact.dummy.swing.domain;

import lombok.*;

import javax.persistence.Id;
import javax.persistence.Version;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "zord_svc")
public class MobilePhoneService extends BaseEntity {
  @Id
  @Column(name = "svc_mgmt_num")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long svcMgmtNum;

  @Version
  private Integer version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cust_num")
  private Customer customer;

  @Column(nullable = false, name = "svc_num")
  private String svcNum;

  @Column(nullable = false, name = "svc_scrb_dt")
  private LocalDate svcScrbDt;
  
  @Column(name = "svc_term_dt")
  private LocalDate svcTermDt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "fee_prod_id")
  private Product feeProduct;

  @Builder
  private MobilePhoneService(Customer customer,
                             String svcNum,
                             LocalDate svcScrbDt,
                             Product feeProduct) {
    this.customer = customer;
    this.svcNum = svcNum;
    this.svcScrbDt = svcScrbDt;
    this.feeProduct = feeProduct;
  }

  public static MobilePhoneService subscribe(Customer customer,
                                             String svcNum,
                                             LocalDate svcScrbDt,
                                             Product feeProduct) {
    return MobilePhoneService.builder()
        .customer(customer)
        .svcNum(svcNum)
        .svcScrbDt(svcScrbDt)
        .feeProduct(feeProduct)
        .build();
  }
  
  public void terminate(LocalDateTime termDtm) {
    svcTermDt = termDtm.toLocalDate();
  }
  
  public boolean validateCustomer(long custNum) {
    return customer.getCustNum() == custNum;
  }
  
  public boolean validateProduct(String prodId) {
    return feeProduct.getProdId().equals(prodId);
  }
}
