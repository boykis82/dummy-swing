package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReqRelMobilePhoneAndOlmagoCustDto {
  private long svcMgmtNum;
  private long olmagoCustomerId;
  private LocalDateTime eventDateTime;
  
  @Builder
  ReqRelMobilePhoneAndOlmagoCustDto(long svcMgmtNum, long olmagoCustomerId, LocalDateTime eventDateTime) {
    this.svcMgmtNum = svcMgmtNum;
    this.olmagoCustomerId = olmagoCustomerId;
    this.eventDateTime = eventDateTime;
  }
}
