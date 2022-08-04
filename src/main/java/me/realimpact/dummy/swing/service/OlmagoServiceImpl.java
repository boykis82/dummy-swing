package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Util;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.ReqRelSvcAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import me.realimpact.dummy.swing.dto.SvcAndOlmagoRelationResponseDto;
import me.realimpact.dummy.swing.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.realimpact.dummy.swing.exception.BusinessExceptionReason.*;

@Service
public class OlmagoServiceImpl implements OlmagoService {
  @Autowired
  MobilePhoneServiceRepository serviceRepository;
  
  @Autowired
  OlmagoCustomerRepository olmagoCustomerRepository;
  
  @Autowired
  ServiceOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository;
  
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
  public SvcAndOlmagoRelationResponseDto linkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto dto) {
    /*
      1. 서비스관리번호, 얼마고고객ID 유효성 체크
      2. 최초 연결 시에는 얼마고고객이 없으므로, 없을 땐 얼마고고객 생성
      3. 얼마고고객에 연결된 swing고객과 서비스의 명의고객이 다르면 오류
      4. 서비스, 얼마고고객 둘 중 하나라도 유효한 릴레이션이 있다면 오류
      5. 새로운 이력 만들어서 저장
     */
    MobilePhoneService mps = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    OlmagoCustomer oc = olmagoCustomerRepository.findById(dto.getOlmagoCustomerId())
        .orElseGet(() -> createOlmagoCustomer(mps, dto.getOlmagoCustomerId()));
    
    if (!mps.getCustomer().equals(oc.getSwingCustomer())) {
      throw new BusinessException(CUSTOMER_MISMATCH, mps.getCustomer().getCustNum(), dto.getOlmagoCustomerId(), oc.getSwingCustomer().getCustNum());
    }
    if (svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(mps, oc, Util.LocalDateTimeMax).size() > 0) {
      throw new BusinessException(SERVICE_OLMAGO_RELATION_EXISTED, dto.getSvcMgmtNum(), dto.getOlmagoCustomerId());
    }
  
    ServiceOlmagoCustomerRelationHistory socrh =
        ServiceOlmagoCustomerRelationHistory.newHistory(mps, oc, dto.getEventDateTime());
    return SvcAndOlmagoRelationResponseDto.of(svcOlmagoCustRelHstRepository.save(socrh));
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
  public SvcAndOlmagoRelationResponseDto unlinkOlmagoCustomerWithMobilePhoneService(ReqRelSvcAndOlmagoCustDto dto) {
    /*
      1. 서비스관리번호, 얼마고고객ID 유효성 체크
      2. 서비스, 얼마고고객 관계가 없으면 오류
      3. 기존 이력 종료하고 저장
     */    
    MobilePhoneService mobilePhoneServiceService = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    OlmagoCustomer olmagoCustomer = olmagoCustomerRepository.findById(dto.getOlmagoCustomerId())
        .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_BY_EXT_REF, dto.getOlmagoCustomerId()));
  
    ServiceOlmagoCustomerRelationHistory socrh =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(mobilePhoneServiceService, olmagoCustomer, Util.LocalDateTimeMax)
            .orElseThrow(() -> new BusinessException(SERVICE_OLMAGO_RELATION_NOT_EXISTED, dto.getSvcMgmtNum(), dto.getOlmagoCustomerId()));
  
    socrh.terminate(dto.getEventDateTime());
    return SvcAndOlmagoRelationResponseDto.of(svcOlmagoCustRelHstRepository.save(socrh));
  }
}
