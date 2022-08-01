package me.realimpact.dummy.swing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReqRelSvcAndOlmagoCustDto {
  private long svcMgmtNum;
  private long olmagoCustomerId;
  private LocalDateTime eventDateTime;
}
