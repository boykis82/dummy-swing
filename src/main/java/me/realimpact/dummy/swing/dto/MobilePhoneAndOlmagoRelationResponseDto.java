package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;
import me.realimpact.dummy.swing.util.Util;
import me.realimpact.dummy.swing.domain.MobilePhoneOlmagoCustomerRelationHistory;

import java.time.LocalDateTime;

@Data
public class MobilePhoneAndOlmagoRelationResponseDto {
  private long svcMgmtNum;
  private long olmagoCustomerId;
  private LocalDateTime eventDataTime;
  
  @Builder
  public MobilePhoneAndOlmagoRelationResponseDto(long svcMgmtNum, long olmagoCustomerId, LocalDateTime eventDataTime) {
    this.svcMgmtNum = svcMgmtNum;
    this.olmagoCustomerId = olmagoCustomerId;
    this.eventDataTime = eventDataTime;
  }
  
  public static MobilePhoneAndOlmagoRelationResponseDto of(MobilePhoneOlmagoCustomerRelationHistory socrh) {
    return MobilePhoneAndOlmagoRelationResponseDto.builder()
        .svcMgmtNum(socrh.getMobilePhone().getSvcMgmtNum())
        .olmagoCustomerId(socrh.getOlmagoCustomer().getOlmagoCustId())
        .eventDataTime(
            socrh.getEffEndDtm().equals(Util.LocalDateTimeMax)
                ? socrh.getEffStaDtm()
                : socrh.getEffEndDtm())
        .build();
  }
}
