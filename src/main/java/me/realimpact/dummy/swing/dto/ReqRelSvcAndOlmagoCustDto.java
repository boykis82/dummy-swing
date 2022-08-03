package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReqRelSvcAndOlmagoCustDto {
  private long svcMgmtNum;
  private long olmagoCustomerId;
  private LocalDateTime eventDateTime;
  
  @Builder
  ReqRelSvcAndOlmagoCustDto(long svcMgmtNum, long olmagoCustomerId, LocalDateTime eventDateTime) {
    this.svcMgmtNum = svcMgmtNum;
    this.olmagoCustomerId = olmagoCustomerId;
    this.eventDateTime = eventDateTime;
  }
}
