package me.realimpact.dummy.swing.proxy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.realimpact.dummy.swing.domain.MobilePhone;

@Getter
public class MobilePhoneDto {
  private final long svcMgmtNum;
  private final String phoneNumber;
  private final String productName;
  private final String mobilePhonePricePlan;
  private final String dcTargetUzooPassProductCode;
  
  public static MobilePhoneDto of(MobilePhone entity) {
    return MobilePhoneDto.builder()
        .phoneNumber(entity.getSvcNum())
        .mobilePhonePricePlan(entity.getFeeProduct().getProductTier().name())
        .productName(entity.getFeeProduct().getProdNm())
        .svcMgmtNum(entity.getSvcMgmtNum())
        .dcTargetUzooPassProductCode(entity.getDcTargetUzoopassProductCode())
        .build();
  }
  
  @Builder
  public MobilePhoneDto(long svcMgmtNum, String phoneNumber, String productName, String mobilePhonePricePlan, String dcTargetUzooPassProductCode) {
    this.svcMgmtNum = svcMgmtNum;
    this.phoneNumber = phoneNumber;
    this.productName = productName;
    this.mobilePhonePricePlan = mobilePhonePricePlan;
    this.dcTargetUzooPassProductCode = dcTargetUzooPassProductCode;
  }
}
