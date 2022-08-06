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
  MobilePhoneServiceRepository serviceRepository;
  OlmagoCustomerRepository olmagoCustomerRepository;
  ServiceOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository;
  CustomerRepository customerRepository;
  ProductRepository productRepository;
  
  OlmagoClient olmagoClient;
  
  enum ProductTierChangeType {
    UP, DOWN, SAME
  }
  
  @Autowired
  public MobilePhoneServiceServiceImpl(
      MobilePhoneServiceRepository serviceRepository,
      OlmagoCustomerRepository olmagoCustomerRepository,
      ServiceOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository,
      CustomerRepository customerRepository,
      ProductRepository productRepository,
      OlmagoClient olmagoClient
  ) {
    this.serviceRepository = serviceRepository;
    this.olmagoCustomerRepository = olmagoCustomerRepository;
    this.svcOlmagoCustRelHstRepository = svcOlmagoCustRelHstRepository;
    this.customerRepository = customerRepository;
    this.productRepository = productRepository;
    this.olmagoClient = olmagoClient;
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
    MobilePhoneService mps = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
    if (!mps.validateCustomer(dto.getBfCustNum())) {
      throw new BusinessException(DATA_INTEGRITY_VIOLATION);
    }

    olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer())
        .flatMap(oc -> svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(rel -> {
          rel.terminate(dto.getOwnerChangedDateTime());
          olmagoClient.unlinkMobilePhoneService(rel.getOlmagoCustomer().getOlmagoCustId(), dto.getSvcMgmtNum());
        });
    
    Customer afterCust = customerRepository.findById(dto.getAfCustNum())
        .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_BY_EXT_REF, dto.getAfCustNum()));
    mps.setCustomer(afterCust);
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
    MobilePhoneService mps = serviceRepository.findById(dto.getSvcMgmtNum())
        .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF, dto.getSvcMgmtNum()));
  
    olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer())
        .flatMap(oc -> svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(rel -> {
          rel.terminate(dto.getTerminatedDateTime());
          olmagoClient.unlinkMobilePhoneService(rel.getOlmagoCustomer().getOlmagoCustId(), dto.getSvcMgmtNum());
        });
    mps.terminate(dto.getTerminatedDateTime());
  }
  
  @Transactional
  @Override
  public void changeFeeProduct(ChangeFeeProductRequestDto dto) {
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
  
    olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer())
        .flatMap(oc -> svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(rel -> {
          ProductTierChangeType type = calculateProductTierChangeType(bfProd, afProd);
          if (type == ProductTierChangeType.UP) {
            olmagoClient.applyMobilePhoneLinkedDiscount(rel.getOlmagoCustomer().getOlmagoCustId(), dto.getSvcMgmtNum());
          } else if (type == ProductTierChangeType.DOWN) {
            olmagoClient.cancelMobilePhoneLinkedDiscount(rel.getOlmagoCustomer().getOlmagoCustId(), dto.getSvcMgmtNum());
          }
        });
  }
  
  private ProductTierChangeType calculateProductTierChangeType(Product beforeProduct, Product afterProduct) {
    if (beforeProduct.isMobilePhoneLinkedDiscountTarget() && !afterProduct.isMobilePhoneLinkedDiscountTarget()) {
      return ProductTierChangeType.DOWN;
    } else if (!beforeProduct.isMobilePhoneLinkedDiscountTarget() && afterProduct.isMobilePhoneLinkedDiscountTarget()) {
      return ProductTierChangeType.UP;
    } else {
      return ProductTierChangeType.SAME;
    }
  }
}
