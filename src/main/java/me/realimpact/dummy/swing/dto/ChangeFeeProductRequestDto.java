package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeFeeProductRequestDto {
  private long svcMgmtNum;
  private String bfFeeProdId;
  private String afFeeProdId;
  private LocalDateTime productChangedDateTime;
  
  @Builder
  public ChangeFeeProductRequestDto(long svcMgmtNum, String beforeProdId, String afFeeProdId, LocalDateTime productChangedDateTime) {
    this.svcMgmtNum = svcMgmtNum;
    this.bfFeeProdId = beforeProdId;
    this.afFeeProdId = afFeeProdId;
    this.productChangedDateTime = productChangedDateTime;
  }
}
