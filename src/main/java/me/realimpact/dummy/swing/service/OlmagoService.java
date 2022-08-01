package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import me.realimpact.dummy.swing.dto.ReqRelSvcAndOlmagoCustDto;

import java.util.List;

public interface OlmagoService {
  List<MobilePhoneResponseDto> getServicesByCI(String ci);
  void linkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto reqRelSvcAndOlmagoCustDto);
  void unlinkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto reqRelSvcAndOlmagoCustDto);
}
