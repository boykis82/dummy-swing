package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.*;
import me.realimpact.dummy.swing.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.realimpact.dummy.swing.exception.BusinessExceptionReason.*;

@Service
public class MobilePhoneServiceImpl implements MobilePhoneService {
  private final MobilePhoneRepository mobilePhoneRepository;
  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;

  private final OlmagoCustomerService olmagoCustomerService;

  @Autowired
  public MobilePhoneServiceImpl(
      MobilePhoneRepository serviceRepository,
      CustomerRepository customerRepository,
      ProductRepository productRepository,
      OlmagoCustomerService olmagoCustomerService
  ) {
    this.mobilePhoneRepository = serviceRepository;
    this.customerRepository = customerRepository;
    this.productRepository = productRepository;

    this.olmagoCustomerService = olmagoCustomerService;
  }

  @Override
  @Transactional(readOnly = true)
  public List<MobilePhoneResponseDto> getMobilePhonesByCi(String ci) {
    return mobilePhoneRepository.findByCI(ci)
        .stream()
        .map(MobilePhoneResponseDto::of)
        .collect(Collectors.toList());
  }

  @Transactional
  @Override
  public void changeOwner(ChangeOwnerRequestDto dto) {
    /*
      1. 서비스 없으면 오류
      2. 서비스의 명의가 bfcustnum아니면 오류
      3. afcustnum에 해당되는 고객 없으면 오류
      4. 서비스에 매핑된 고객을 afcust로 변경
      5. bfcustnum에 매핑된 olmago customer와 서비스 간 매핑 있으면 종료
      6. 매핑 이력 있으면 얼마고 시스템으로 연결 끊기 api 호출
     */
    MobilePhone mps = mobilePhoneRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    if (!mps.validateCustomer(dto.getBfCustNum())) {
      throw new BusinessException(DATA_INTEGRITY_VIOLATION);
    }
    Customer afterCust = customerRepository.findById(dto.getAfCustNum())
        .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_BY_EXT_REF, dto.getAfCustNum()));
    mps.setCustomer(afterCust);

    olmagoCustomerService.unlinkWithMobilePhoneService(mps, dto.getOwnerChangedDateTime());
  }
  
  @Transactional
  @Override
  public void terminate(TerminateRequestDto dto) {
    /*
      1. 서비스 없으면 오류
      2. 서비스 해지
      3. 서비스에 매핑된 고객에 매핑된 olmago customer와 서비스 간 매핑 있으면 종료
      4. 매핑 이력 있으면 얼마고 시스템으로 연결 끊기 api 호출
     */
    MobilePhone mps = mobilePhoneRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    mps.terminate(dto.getTerminatedDateTime());

    olmagoCustomerService.unlinkWithMobilePhoneService(mps, dto.getTerminatedDateTime());
  }
  
  @Transactional
  @Override
  public void changeFeeProduct(ChangeFeeProductRequestDto dto) {
    /*
      1. 서비스 없으면 오류
      2. 상품 없으면 오류
      3. 상품 변경
     */
    MobilePhone mps = mobilePhoneRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    if (!mps.validateProduct(dto.getBfFeeProdId())) {
      throw new BusinessException(DATA_INTEGRITY_VIOLATION);
    }
    Product afProd = productRepository.findById(dto.getAfFeeProdId())
        .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND_BY_EXT_REF, dto.getAfFeeProdId()));
    mps.setFeeProduct(afProd);

    olmagoCustomerService.applyMobilePhoneLinkedDiscount(mps, afProd.getProductTier());
  }
}
