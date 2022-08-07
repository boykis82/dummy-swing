package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;
import me.realimpact.dummy.swing.domain.MobilePhone;

import java.time.LocalDate;

@Data
public class MobilePhoneResponseDto {
  private long svcMgmtNum;
  private String svcNum;
  private LocalDate svcScrbDt;
  private String feeProdID;
  private String feeProdNm;
  private boolean isMobilePhoneLinkedDiscountTarget;
  
  @Builder
  private MobilePhoneResponseDto(
    long svcMgmtNum,
    String svcNum,
    LocalDate svcScrbDt,
    String feeProdID,
    String feeProdNm,
    boolean isMobilePhoneLinkedDiscountTarget
  ) {
    this.svcMgmtNum = svcMgmtNum;
    this.svcNum = svcNum;
    this.svcScrbDt = svcScrbDt;
    this.feeProdID = feeProdID;
    this.feeProdNm = feeProdNm;
    this.isMobilePhoneLinkedDiscountTarget = isMobilePhoneLinkedDiscountTarget;
  }
  
  public static MobilePhoneResponseDto of(MobilePhone mobilePhone) {
    return MobilePhoneResponseDto.builder()
        .svcMgmtNum(mobilePhone.getSvcMgmtNum())
        .svcNum(mobilePhone.getSvcNum())
        .svcScrbDt(mobilePhone.getSvcScrbDt())
        .feeProdID(mobilePhone.getFeeProduct().getProdId())
        .feeProdNm(mobilePhone.getFeeProduct().getProdNm())
        .isMobilePhoneLinkedDiscountTarget(mobilePhone.getFeeProduct().isMobilePhoneLinkedDiscountTarget())
        .build();
  }
}
