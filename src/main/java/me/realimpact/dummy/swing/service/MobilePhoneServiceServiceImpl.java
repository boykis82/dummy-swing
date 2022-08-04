package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Util;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.*;
import me.realimpact.dummy.swing.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;

import static me.realimpact.dummy.swing.exception.BusinessExceptionReason.*;

@Service
public class MobilePhoneServiceServiceImpl implements MobilePhoneServiceService {
  @Autowired
  MobilePhoneServiceRepository serviceRepository;
  
  @Autowired
  OlmagoCustomerRepository olmagoCustomerRepository;
  
  @Autowired
  ServiceOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository;
  
  @Autowired
  CustomerRepository customerRepository;
  
  @Autowired
  ProductRepository productRepository;
  
  @Transactional
  @Override
  public ChangeOwnerResponseDto changeOwner(ChangeOwnerRequestDto dto) {
    /*
      1. 서비스 없으면 오류
      2. 서비스의 명의가 bfcustnum아니면 오류
      3. afcustnum에 해당되는 고객 없으면 오류
      4. 서비스에 매핑된 고객을 afcust로 변경
      5. bfcustnum에 매핑된 olmago customer와 서비스 간 매핑 있으면 종료

     */
    MobilePhoneService mps = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    if (!mps.validateCustomer(dto.getBfCustNum())) {
      throw new BusinessException(DATA_INTEGRITY_VIOLATION);
    }

    Optional<OlmagoCustomer> bfOlmagoCustomer = olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer());
    bfOlmagoCustomer.flatMap(oc -> svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(socrh -> socrh.terminate(dto.getOwnerChangedDateTime()));
    
    Customer afterCust = customerRepository.findById(dto.getAfCustNum())
        .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_BY_EXT_REF, dto.getAfCustNum()));
    mps.setCustomer(afterCust);
    
    return ChangeOwnerResponseDto.builder()
        .svcMgmtNum(dto.getSvcMgmtNum())
        .bfCustNum(dto.getBfCustNum())
        .afCustNum(dto.getAfCustNum())
        .ownerChangeDateTime(dto.getOwnerChangedDateTime())
        .bfOlmagoCustomerId(bfOlmagoCustomer.map(OlmagoCustomer::getOlmagoCustId).orElse(0L))
        .build();
  }
  
  @Transactional
  @Override
  public TerminateResponseDto terminate(TerminateRequestDto dto) {
    /*
      1. 서비스 없으면 오류
      2. 서비스 해지
      3. 서비스에 매핑된 고객에 매핑된 olmago customer와 서비스 간 매핑 있으면 종료
     */
    MobilePhoneService mps = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
  
    Optional<OlmagoCustomer> bfOlmagoCustomer = olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer());
    bfOlmagoCustomer.flatMap(oc -> svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(socrh -> socrh.terminate(dto.getTerminatedDateTime()));
    mps.terminate(dto.getTerminatedDateTime());
    
    return TerminateResponseDto.builder()
        .svcMgmtNum(dto.getSvcMgmtNum())
        .terminatedDateTime(dto.getTerminatedDateTime())
        .olmagoCustomerId(bfOlmagoCustomer.map(OlmagoCustomer::getOlmagoCustId).orElse(0L))
        .build();
  }
  
  @Transactional
  @Override
  public ChangeFeeProductResponseDto changeFeeProduct(ChangeFeeProductRequestDto dto) {
    /*
      1. 서비스 없으면 오류
      2. 상품 없으면 오류
      3. 상품 변경
     */
    MobilePhoneService mps = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    if (!mps.validateProduct(dto.getBfFeeProdId())) {
      throw new BusinessException(DATA_INTEGRITY_VIOLATION);
    }
    Product bfProd = mps.getFeeProduct();
    Product afProd = productRepository.findById(dto.getAfFeeProdId())
        .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND_BY_EXT_REF, dto.getAfFeeProdId()));
    mps.setFeeProduct(afProd);
  
    Optional<OlmagoCustomer> bfOlmagoCustomer = olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer());
  
    return ChangeFeeProductResponseDto.builder()
        .svcMgmtNum(dto.getSvcMgmtNum())
        .productChangedDateTime(dto.getProductChangedDateTime())
        .bfFeeProdId(dto.getBfFeeProdId())
        .afFeeProdId(dto.getAfFeeProdId())
        .olmagoCustomerId(bfOlmagoCustomer.map(OlmagoCustomer::getOlmagoCustId).orElse(0L))
        .mobilePhoneProductTierChangeType(calculateProductTierChangeType(bfProd, afProd))
        .build();
  }
  
  private ChangeFeeProductResponseDto.ProductTierChangeType calculateProductTierChangeType(Product beforeProduct, Product afterProduct) {
    if (beforeProduct.isMobilePhoneLinkedDiscountTarget() && !afterProduct.isMobilePhoneLinkedDiscountTarget()) {
      return ChangeFeeProductResponseDto.ProductTierChangeType.DOWN;
    } else if (!beforeProduct.isMobilePhoneLinkedDiscountTarget() && afterProduct.isMobilePhoneLinkedDiscountTarget()) {
      return ChangeFeeProductResponseDto.ProductTierChangeType.UP;
    } else {
      return ChangeFeeProductResponseDto.ProductTierChangeType.SAME;
    }
  }
}
