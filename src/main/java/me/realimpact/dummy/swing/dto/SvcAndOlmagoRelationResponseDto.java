package me.realimpact.dummy.swing.dto;

import lombok.Builder;
import lombok.Data;
import me.realimpact.dummy.swing.Util;
import me.realimpact.dummy.swing.domain.ServiceOlmagoCustomerRelationHistory;

import java.time.LocalDateTime;

@Data
public class SvcAndOlmagoRelationResponseDto {
  private long svcMgmtNum;
  private long olmagoCustomerId;
  private LocalDateTime eventDataTime;
  
  @Builder
  public SvcAndOlmagoRelationResponseDto(long svcMgmtNum, long olmagoCustomerId, LocalDateTime eventDataTime) {
    this.svcMgmtNum = svcMgmtNum;
    this.olmagoCustomerId = olmagoCustomerId;
    this.eventDataTime = eventDataTime;
  }
  
  public static SvcAndOlmagoRelationResponseDto of(ServiceOlmagoCustomerRelationHistory socrh) {
    return SvcAndOlmagoRelationResponseDto.builder()
        .svcMgmtNum(socrh.getMobilePhoneService().getSvcMgmtNum())
        .olmagoCustomerId(socrh.getOlmagoCustomer().getOlmagoCustId())
        .eventDataTime(
            socrh.getEffEndDtm().equals(Util.LocalDateTimeMax)
                ? socrh.getEffStaDtm()
                : socrh.getEffEndDtm())
        .build();
  }
}
