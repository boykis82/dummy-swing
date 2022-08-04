package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeFeeProductResponseDto {
  public enum ProductTierChangeType {
    UP, DOWN, SAME
  }
  
  private long svcMgmtNum;
  private String bfFeeProdId;
  private ProductTierChangeType mobilePhoneProductTierChangeType;
  private String afFeeProdId;
  private LocalDateTime productChangedDateTime;
  private long olmagoCustomerId;
  
  @Builder
  public ChangeFeeProductResponseDto(
      long svcMgmtNum,
      String bfFeeProdId,
      String afFeeProdId,
      LocalDateTime productChangedDateTime,
      ProductTierChangeType mobilePhoneProductTierChangeType,
      long olmagoCustomerId
  ) {
    this.svcMgmtNum = svcMgmtNum;
    this.bfFeeProdId = bfFeeProdId;
    this.afFeeProdId = afFeeProdId;
    this.productChangedDateTime = productChangedDateTime;
    this.mobilePhoneProductTierChangeType = mobilePhoneProductTierChangeType;
    this.olmagoCustomerId = olmagoCustomerId;
  }
}
