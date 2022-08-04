package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeOwnerResponseDto {
  private long svcMgmtNum;
  private long bfCustNum;
  private long afCustNum;
  private long bfOlmagoCustomerId;
  private LocalDateTime ownerChangedDateTime;
  
  @Builder
  public ChangeOwnerResponseDto(long svcMgmtNum, long bfCustNum, long afCustNum, long bfOlmagoCustomerId, LocalDateTime ownerChangeDateTime) {
    this.svcMgmtNum = svcMgmtNum;
    this.bfCustNum = bfCustNum;
    this.afCustNum = afCustNum;
    this.bfOlmagoCustomerId = bfOlmagoCustomerId;
    this.ownerChangedDateTime = ownerChangeDateTime;
  }
  
}
