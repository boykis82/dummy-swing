package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;
import me.realimpact.dummy.swing.domain.MobilePhoneService;

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
  
  public static MobilePhoneResponseDto of(MobilePhoneService svc) {
    return MobilePhoneResponseDto.builder()
        .svcMgmtNum(svc.getSvcMgmtNum())
        .svcNum(svc.getSvcNum())
        .svcScrbDt(svc.getSvcScrbDt())
        .feeProdID(svc.getFeeProduct().getProdId())
        .feeProdNm(svc.getFeeProduct().getProdNm())
        .isMobilePhoneLinkedDiscountTarget(svc.getFeeProduct().isMobilePhoneLinkedDiscountTarget())
        .build();
  }
}
