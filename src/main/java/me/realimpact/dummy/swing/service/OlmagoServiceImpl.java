package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.util.Util;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.ReqRelMobilePhoneAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.MobilePhoneAndOlmagoRelationResponseDto;
import me.realimpact.dummy.swing.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static me.realimpact.dummy.swing.exception.BusinessExceptionReason.*;

@Service
public class OlmagoServiceImpl implements OlmagoService {
  private final MobilePhoneRepository serviceRepository;
  private final OlmagoCustomerRepository olmagoCustomerRepository;
  private final MobilePhoneOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository;
  
  @Autowired
  public OlmagoServiceImpl(
      MobilePhoneRepository serviceRepository,
      OlmagoCustomerRepository olmagoCustomerRepository,
      MobilePhoneOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository
  ) {
    this.serviceRepository = serviceRepository;
    this.olmagoCustomerRepository = olmagoCustomerRepository;
    this.svcOlmagoCustRelHstRepository = svcOlmagoCustRelHstRepository;
  }

  @Override
  @Transactional
  public MobilePhoneAndOlmagoRelationResponseDto linkOlmagoCustomerWithMobilePhoneService(ReqRelMobilePhoneAndOlmagoCustDto dto) {
    /*
      1. 서비스관리번호, 얼마고고객ID 유효성 체크
      2. 최초 연결 시에는 얼마고고객이 없으므로, 없을 땐 얼마고고객 생성
      3. 얼마고고객에 연결된 swing고객과 서비스의 명의고객이 다르면 오류
      4. 서비스, 얼마고고객 둘 중 하나라도 유효한 릴레이션이 있다면 오류
      5. 새로운 이력 만들어서 저장
     */
    MobilePhone mps = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    OlmagoCustomer oc = olmagoCustomerRepository.findById(dto.getOlmagoCustomerId())
        .orElseGet(() -> createOlmagoCustomer(mps, dto.getOlmagoCustomerId()));

    if (!mps.validateCustomer(oc.getSwingCustomer().getCustNum())) {
      throw new BusinessException(CUSTOMER_MISMATCH, mps.getCustomer().getCustNum(), dto.getOlmagoCustomerId(), oc.getSwingCustomer().getCustNum());
    }
    if (svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(mps, oc, Util.LocalDateTimeMax).size() > 0) {
      throw new BusinessException(SERVICE_OLMAGO_RELATION_EXISTED, dto.getSvcMgmtNum(), dto.getOlmagoCustomerId());
    }
  
    MobilePhoneOlmagoCustomerRelationHistory socrh =
        MobilePhoneOlmagoCustomerRelationHistory.newHistory(mps, oc, dto.getEventDateTime());
    return MobilePhoneAndOlmagoRelationResponseDto.of(svcOlmagoCustRelHstRepository.save(socrh));
  }
  
  private OlmagoCustomer createOlmagoCustomer(MobilePhone service, long olmagoCustomerId) {
    OlmagoCustomer olmagoCustomer = OlmagoCustomer.builder()
        .swingCustomer(service.getCustomer())
        .olmagoCustId(olmagoCustomerId)
        .build();
    return olmagoCustomerRepository.save(olmagoCustomer);
  }
  
  @Override
  @Transactional
  public MobilePhoneAndOlmagoRelationResponseDto unlinkOlmagoCustomerWithMobilePhoneService(ReqRelMobilePhoneAndOlmagoCustDto dto) {
    /*
      1. 서비스관리번호, 얼마고고객ID 유효성 체크
      2. 서비스, 얼마고고객 관계가 없으면 오류
      3. 기존 이력 종료하고 저장
     */    
    MobilePhone mobilePhoneService = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    OlmagoCustomer olmagoCustomer = olmagoCustomerRepository.findById(dto.getOlmagoCustomerId())
        .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_BY_EXT_REF, dto.getOlmagoCustomerId()));
  
    MobilePhoneOlmagoCustomerRelationHistory socrh =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(mobilePhoneService, olmagoCustomer, Util.LocalDateTimeMax)
            .orElseThrow(() -> new BusinessException(SERVICE_OLMAGO_RELATION_NOT_EXISTED, dto.getSvcMgmtNum(), dto.getOlmagoCustomerId()));
  
    socrh.terminate(dto.getEventDateTime());
    return MobilePhoneAndOlmagoRelationResponseDto.of(svcOlmagoCustRelHstRepository.save(socrh));
  }
}
