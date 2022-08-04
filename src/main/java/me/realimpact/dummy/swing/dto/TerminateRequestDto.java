package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TerminateRequestDto {
  private long svcMgmtNum;
  private LocalDateTime terminatedDateTime;
  
  @Builder
  public TerminateRequestDto(long svcMgmtNum, LocalDateTime terminatedDateTime) {
    this.svcMgmtNum = svcMgmtNum;
    this.terminatedDateTime = terminatedDateTime;
  }
}
