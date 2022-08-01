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
@Table(name = "zord_olmago_cust_rel_hst")
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

  @Column(nullable = false)
  private long olmagoCustomerID;

  @ManyToOne
  @JoinColumn(name = "svc_mgmt_num")
  private Service service;

  @Builder
  public OlmagoCustomerRelationHistory(
      long olmagoCustomerID,
      LocalDateTime effStaDtm,
      Service service
  ) {
    this.olmagoCustomerID = olmagoCustomerID;
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
}
