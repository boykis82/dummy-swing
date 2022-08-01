package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.dto.RequestOlmagoCustomerRelationDto;
import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import me.realimpact.dummy.swing.persistence.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OlmagoService {
  @Autowired
  ServiceRepository serviceRepository;

  @Transactional(readOnly = true)
  public List<MobilePhoneResponseDto> getServicesByCI(String ci) {
    return serviceRepository.findByCI(ci);
  }

  @Transactional
  public void linkOlmagoCustomerWithMobilePhoneService(
      long svcMgmtNum,
      RequestOlmagoCustomerRelationDto requestOlmagoCustomerRelationDto
  ) {

  }

  @Transactional
  public void unlinkOlmagoCustomerWithMobilePhoneService(
      long svcMgmtNum,
      RequestOlmagoCustomerRelationDto requestOlmagoCustomerRelationDto
  ) {

  }
}
