package me.realimpact.dummy.swing.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "zord_svc_olmago_cust_rel_hst",
    indexes = @Index(name = "i_svc_olmago_cust", columnList = "svc_mgmt_num, olmago_cust_num, eff_end_dtm DESC", unique = true)
)
public class OlmagoCustomerRelationHistory {
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
  @JoinColumn(name = "olmago_cust_num")
  private OlmagoCustomer olmagoCustomer;

  @ManyToOne
  @JoinColumn(name = "svc_mgmt_num")
  private Service service;

  @Builder
  public OlmagoCustomerRelationHistory(
      OlmagoCustomer olmagoCustomer,
      Service service,
      LocalDateTime effStaDtm
  ) {
    this.olmagoCustomer = olmagoCustomer;
    this.effStaDtm = effStaDtm;
    this.effEndDtm = LocalDateTime.MAX;
    this.service = service;
  }

  public boolean terminated() {
    return !isLastHistory();
  }

  public void terminate(LocalDateTime termDtm) {
    effEndDtm = termDtm;
  }

  public boolean isLastHistory() {
    return effEndDtm.isEqual(LocalDateTime.MAX);
  }

  public void linkSwingService(long olmagoCustomerID, LocalDateTime linkDateTime, Service service) {
    terminateOlmagoCustomerRelation(linkDateTime.minusSeconds(1));
    addOlmagoCustomerRelation(olmagoCustomerID, linkDateTime);
  }

  public void unlinkSwingService(LocalDateTime unlinkDateTime, Service service) {
    terminateOlmagoCustomerRelation(unlinkDateTime);
  }

  private void terminateOlmagoCustomerRelation(LocalDateTime unlinkDateTime) {
    olmagoCustomerRelationHistories.stream()
        .filter(OlmagoCustomerRelationHistory::isLastHistory)
        .findFirst()
        .ifPresent(ocrh -> ocrh.terminate(unlinkDateTime));
  }

  private void addOlmagoCustomerRelation(long olmagoCustomerID, LocalDateTime linkDateTime) {
    olmagoCustomerRelationHistories.add(
        OlmagoCustomerRelationHistory.builder()
            .olmagoCustomerID(olmagoCustomerID)
            .effStaDtm(linkDateTime)
            .build()
    );
  }

}
