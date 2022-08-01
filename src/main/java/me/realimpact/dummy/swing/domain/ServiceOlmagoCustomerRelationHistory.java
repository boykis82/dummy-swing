package me.realimpact.dummy.swing.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
    name = "zord_svc_olmago_cust_rel_hst",
    indexes = @Index(name = "i_svc_olmago_cust", columnList = "mobilePhoneService, olmagoCustomer, effEndDtm DESC", unique = true)
)
public class ServiceOlmagoCustomerRelationHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  private Integer version;

  @Column(nullable = false)
  LocalDateTime effStaDtm;

  @Column(nullable = false)
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
