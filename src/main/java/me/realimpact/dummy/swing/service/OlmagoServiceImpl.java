package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Util;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.ReqRelSvcAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class OlmagoServiceImpl implements OlmagoService {
  @Autowired
  MobilePhoneServiceRepository serviceRepository;
  
  @Autowired
  OlmagoCustomerRepository olmagoCustomerRepository;
  
  @Autowired
  ServiceOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository;
  
  @Autowired
  CustomerRepository customerRepository;
  
  @Override
  @Transactional(readOnly = true)
  public List<MobilePhoneResponseDto> getServicesByCI(String ci) {
    return serviceRepository.findByCI(ci)
        .stream()
        .map(MobilePhoneResponseDto::of)
        .collect(Collectors.toList());
  }
  
  @Override
  @Transactional
  public void linkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto dto) {
    MobilePhoneService mobilePhoneService = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new RuntimeException("주어진 서비스관리번호로 서비스가 존재하지 않습니다!"));
    OlmagoCustomer olmagoCustomer = olmagoCustomerRepository.findById(dto.getOlmagoCustomerId())
            .orElseGet(() -> createOlmagoCustomer(mobilePhoneService, dto.getOlmagoCustomerId()));
    
    if (svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(mobilePhoneService, olmagoCustomer, Util.LocalDateTimeMax).size() > 0) {
      throw new RuntimeException("유효한 서비스-얼마고 고객 관계이력이 존재합니다! 먼저 연결을 끊어주세요!");
    }
  
    ServiceOlmagoCustomerRelationHistory socrh =
        ServiceOlmagoCustomerRelationHistory.newHistory(mobilePhoneService, olmagoCustomer, dto.getEventDateTime());
    svcOlmagoCustRelHstRepository.save(socrh);
  }
  
  private OlmagoCustomer createOlmagoCustomer(MobilePhoneService service, long olmagoCustomerId) {
    Customer swingCustomer = service.getCustomer();
    OlmagoCustomer olmagoCustomer = OlmagoCustomer.builder()
        .swingCustomer(swingCustomer)
        .olmagoCustId(olmagoCustomerId)
        .build();
    return olmagoCustomerRepository.save(olmagoCustomer);
  }
  
  @Override
  @Transactional
  public void unlinkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto dto) {
    MobilePhoneService mobilePhoneService = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new RuntimeException("주어진 서비스관리번호로 서비스가 존재하지 않습니다!"));
    OlmagoCustomer olmagoCustomer = olmagoCustomerRepository.findById(dto.getOlmagoCustomerId())
        .orElseThrow(() -> new RuntimeException("주어진 얼마고 고객ID가 존재하지 않습니다!"));
  
    ServiceOlmagoCustomerRelationHistory socrh =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(mobilePhoneService, olmagoCustomer, Util.LocalDateTimeMax)
            .orElseThrow(() -> new RuntimeException("주어진 서비스관리번호-얼마고고객ID로 유효한 이력이 존재하지 않습니다!"));
  
    socrh.terminate(dto.getEventDateTime());
    svcOlmagoCustRelHstRepository.save(socrh);
  }
}
