package me.realimpact.dummy.swing.domain;

import lombok.*;
import javax.persistence.Id;
import javax.persistence.Version;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Table(
    name = "zord_svc_olmago_cust_rel_hst",
    indexes = @Index(name = "i_svc_olmago_cust", columnList = "svc_mgmt_num, olmago_cust_id, eff_end_dtm DESC", unique = true)
)
public class ServiceOlmagoCustomerRelationHistory extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  private Integer version;

  @Column(nullable = false, name = "eff_sta_dtm")
  LocalDateTime effStaDtm;

  @Column(nullable = false, name = "eff_end_dtm")
  LocalDateTime effEndDtm;

  @ManyToOne
  @JoinColumn(name = "svc_mgmt_num")
  private MobilePhoneService mobilePhoneService;
  
  @ManyToOne
  @JoinColumn(name = "olmago_cust_id")
  private OlmagoCustomer olmagoCustomer;

  @Builder
  private ServiceOlmagoCustomerRelationHistory(
      MobilePhoneService mobilePhoneService,
      OlmagoCustomer olmagoCustomer,
      LocalDateTime effStaDtm
  ) {
    this.mobilePhoneService = mobilePhoneService;
    this.olmagoCustomer = olmagoCustomer;
    this.effStaDtm = effStaDtm;
    this.effEndDtm = LocalDateTime.MAX;
  }
  
  public static ServiceOlmagoCustomerRelationHistory newHistory(
      MobilePhoneService mobilePhoneService,
      OlmagoCustomer olmagoCustomer,
      LocalDateTime effStaDtm
  ) {
    return ServiceOlmagoCustomerRelationHistory.builder()
        .mobilePhoneService(mobilePhoneService)
        .olmagoCustomer(olmagoCustomer)
        .effStaDtm(effStaDtm)
        .build();
  }

  public boolean terminated() {
    return !isActiveHistory();
  }

  public void terminate(LocalDateTime termDtm) {
    effEndDtm = termDtm;
  }

  public boolean isActiveHistory() {
    return effEndDtm.isEqual(LocalDateTime.MAX);
  }

}
