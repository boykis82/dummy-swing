package me.realimpact.dummy.swing.dto;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class RequestOlmagoCustomerRelationDto {
  private long olmagoCustomerId;
  private LocalDateTime eventDateTime;
}
