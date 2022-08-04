package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeOwnerRequestDto {
  private long svcMgmtNum;
  private long bfCustNum;
  private long afCustNum;
  private LocalDateTime ownerChangedDateTime;
  
  @Builder
  public ChangeOwnerRequestDto(long svcMgmtNum, long bfCustNum, long afCustNum, LocalDateTime ownerChangeDateTime) {
    this.svcMgmtNum = svcMgmtNum;
    this.bfCustNum = bfCustNum;
    this.afCustNum = afCustNum;
    this.ownerChangedDateTime = ownerChangeDateTime;
  }
}
