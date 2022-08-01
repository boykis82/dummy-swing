package me.realimpact.dummy.swing.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.bytebuddy.asm.Advice;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "zord_svc")
public class Service {
  @Id
  @Column(name = "svc_mgmt_num")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long svcMgmtNum;

  @Version
  private Integer version;

  @ManyToOne
  @JoinColumn(name = "cust_num")
  private Customer customer;

  @Column(nullable = false)
  private String svcNum;

  @Column(nullable = false)
  private LocalDate svcScrbDt;

  private LocalDate svcTermDt;

  @ManyToOne
  @JoinColumn(name = "fee_prod_id")
  private Product feeProduct;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "service", cascade = CascadeType.ALL)
  private List<OlmagoCustomerRelationHistory> olmagoCustomerRelationHistories = new ArrayList<>();

  @Builder
  public Service(Customer customer,
                 String svcNum,
                 LocalDate svcScrbDt) {
    this.customer = customer;
    this.svcNum = svcNum;
    this.svcScrbDt = svcScrbDt;
  }

  public void linkOlmagoCustomer(long olmagoCustomerID, LocalDateTime linkDateTime) {
    terminateOlmagoCustomerRelation(linkDateTime.minusSeconds(1));
    addOlmagoCustomerRelation(olmagoCustomerID, linkDateTime);
  }

  public void unlinkOlmagoCustomer(LocalDateTime unlinkDateTime) {
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
