package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import me.realimpact.dummy.swing.dto.ReqRelSvcAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.SvcAndOlmagoRelationResponseDto;

import java.util.List;

public interface OlmagoService {
  List<MobilePhoneResponseDto> getServicesByCI(String ci);
  SvcAndOlmagoRelationResponseDto linkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto reqRelSvcAndOlmagoCustDto);
  SvcAndOlmagoRelationResponseDto unlinkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto reqRelSvcAndOlmagoCustDto);
}
