package me.realimpact.dummy.swing.service;

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
  public void linkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto reqRelSvcAndOlmagoCustDto) {
    MobilePhoneService mobilePhoneService = serviceRepository.findById(reqRelSvcAndOlmagoCustDto.getSvcMgmtNum())
        .orElseThrow(() -> new RuntimeException("주어진 서비스관리번호로 서비스가 존재하지 않습니다!"));
    OlmagoCustomer olmagoCustomer = olmagoCustomerRepository.findById(reqRelSvcAndOlmagoCustDto.getOlmagoCustomerId())
            .orElseGet(() -> createOlmagoCustomer(mobilePhoneService, reqRelSvcAndOlmagoCustDto.getOlmagoCustomerId()));
    
    if (svcOlmagoCustRelHstRepository.findActiveHistoryByServiceOrOlmagoCustomer(mobilePhoneService, olmagoCustomer).size() > 0) {
      throw new RuntimeException("유효한 서비스-얼마고 고객 관계이력이 존재합니다! 먼저 연결을 끊어주세요!");
    }
  
    ServiceOlmagoCustomerRelationHistory socrh =
        ServiceOlmagoCustomerRelationHistory.newHistory(mobilePhoneService, olmagoCustomer, reqRelSvcAndOlmagoCustDto.getEventDateTime());
    svcOlmagoCustRelHstRepository.save(socrh);
  }
  
  private OlmagoCustomer createOlmagoCustomer(MobilePhoneService mobilePhoneService, long olmagoCustomerId) {
    Customer swingCustomer = mobilePhoneService.getCustomer();
    OlmagoCustomer olmagoCustomer = OlmagoCustomer.builder()
        .swingCustomer(swingCustomer)
        .olmagoCustId(olmagoCustomerId)
        .build();
    return olmagoCustomerRepository.save(olmagoCustomer);
  }
  
  @Override
  @Transactional
  public void unlinkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto reqRelSvcAndOlmagoCustDto) {
    MobilePhoneService mobilePhoneService = serviceRepository.findById(reqRelSvcAndOlmagoCustDto.getSvcMgmtNum())
        .orElseThrow(() -> new RuntimeException("주어진 서비스관리번호로 서비스가 존재하지 않습니다!"));
    OlmagoCustomer olmagoCustomer = olmagoCustomerRepository.findById(reqRelSvcAndOlmagoCustDto.getOlmagoCustomerId())
        .orElseThrow(() -> new RuntimeException("주어진 얼마고 고객ID가 존재하지 않습니다!"));
  
    ServiceOlmagoCustomerRelationHistory socrh =
        svcOlmagoCustRelHstRepository.findActiveHistoryByServiceAndOlmagoCustomer(mobilePhoneService, olmagoCustomer)
            .orElseThrow(() -> new RuntimeException("주어진 서비스관리번호-얼마고고객ID로 유효한 이력이 존재하지 않습니다!"));
  
    socrh.terminate(reqRelSvcAndOlmagoCustDto.getEventDateTime());
  }
}
