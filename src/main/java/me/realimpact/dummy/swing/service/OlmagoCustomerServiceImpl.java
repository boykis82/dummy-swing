package me.realimpact.dummy.swing.service;

import lombok.NoArgsConstructor;
import me.realimpact.dummy.swing.domain.Product.ProductTier;
import me.realimpact.dummy.swing.util.Util;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.proxy.OlmagoProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@NoArgsConstructor
public class OlmagoCustomerServiceImpl implements OlmagoCustomerService {
  private OlmagoCustomerRepository olmagoCustomerRepository;
  private MobilePhoneOlmagoCustomerRelationHistoryRepository mobilePhoneOlmagoCustRelHstRepository;

  private OlmagoProxy olmagoProxy;

  long timeoutSeconds;

  @Autowired
  public OlmagoCustomerServiceImpl(
      OlmagoCustomerRepository olmagoCustomerRepository,
      MobilePhoneOlmagoCustomerRelationHistoryRepository mobilePhoneOlmagoCustRelHstRepository,
      OlmagoProxy olmagoProxy,
      @Value("${app.timeout-seconds}") long timeoutSeconds
  ) {
    this.olmagoCustomerRepository = olmagoCustomerRepository;
    this.mobilePhoneOlmagoCustRelHstRepository = mobilePhoneOlmagoCustRelHstRepository;

    this.olmagoProxy = olmagoProxy;
    this.timeoutSeconds = timeoutSeconds;
  }

  @Transactional
  @Override
  public void unlinkWithMobilePhoneService(MobilePhone mps, LocalDateTime unlinkDateTime) {
    olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer())
        .flatMap(oc -> mobilePhoneOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(rel -> {
          rel.terminate(unlinkDateTime);
          olmagoProxy.unlinkMobilePhoneService(
              rel.getOlmagoCustomer().getOlmagoCustId(),
              rel.getMobilePhone().getSvcMgmtNum()
          ).block(Duration.ofSeconds(timeoutSeconds));
        });
  }

  @Transactional
  @Override
  public void applyMobilePhoneLinkedDiscount(MobilePhone mps, ProductTier productTier) {
    olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer())
        .flatMap(oc -> mobilePhoneOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(rel -> olmagoProxy.applyMobilePhoneLinkedDiscount(
            rel.getOlmagoCustomer().getOlmagoCustId(),
            rel.getMobilePhone().getSvcMgmtNum(),
            productTier
            ).block(Duration.ofSeconds(timeoutSeconds))
        );
  }
}
