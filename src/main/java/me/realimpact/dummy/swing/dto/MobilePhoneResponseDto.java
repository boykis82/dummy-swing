package me.realimpact.dummy.swing.dto;

import me.realimpact.dummy.swing.persistence.Customer;
import org.springframework.data.annotation.Version;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

public class MobilePhoneResponseDto {
  private long svcMgmtNum;
  private String svcNum;
  private LocalDate svcScrbDt;

}
