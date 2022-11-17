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
public class MobilePhone extends BaseEntity {
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
  
  @Column(name = "dc_uzoopass_prod_cd")
  private String dcTargetUzoopassProductCode;

  @Builder
  private MobilePhone(Customer customer,
                      String svcNum,
                      LocalDate svcScrbDt,
                      Product feeProduct,
                      String dcTargetUzoopassProductCode) {
    this.customer = customer;
    this.svcNum = svcNum;
    this.svcScrbDt = svcScrbDt;
    this.feeProduct = feeProduct;
    this.dcTargetUzoopassProductCode = dcTargetUzoopassProductCode;
  }

  public static MobilePhone subscribe(Customer customer,
                                      String svcNum,
                                      LocalDate svcScrbDt,
                                      Product feeProduct,
                                      String dcTargetUzoopassProductCode) {
    return MobilePhone.builder()
        .customer(customer)
        .svcNum(svcNum)
        .svcScrbDt(svcScrbDt)
        .feeProduct(feeProduct)
        .dcTargetUzoopassProductCode(dcTargetUzoopassProductCode)
        .build();
  }
  
  public static MobilePhone subscribe(Customer customer,
                                      String svcNum,
                                      LocalDate svcScrbDt,
                                      Product feeProduct) {
    return MobilePhone.subscribe(customer, svcNum, svcScrbDt, feeProduct, null);
  }
  
  public void terminate(LocalDateTime termDtm) {
    svcTermDt = termDtm.toLocalDate();
  }
  
  public boolean validateCustomer(Long custNum) {
    return customer.getCustNum().equals(custNum);
  }
  
  public boolean validateProduct(String prodId) {
    return feeProduct.getProdId().equals(prodId);
  }
}
