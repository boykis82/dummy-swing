package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TerminateResponseDto {
  private long svcMgmtNum;
  private LocalDateTime terminatedDateTime;
  private long olmagoCustomerId;
  
  @Builder
  public TerminateResponseDto(long svcMgmtNum, LocalDateTime terminatedDateTime, long olmagoCustomerId) {
    this.svcMgmtNum = svcMgmtNum;
    this.terminatedDateTime = terminatedDateTime;
    this.olmagoCustomerId = olmagoCustomerId;
  }
}
