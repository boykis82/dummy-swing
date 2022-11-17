package me.realimpact.dummy.swing.service;

import lombok.RequiredArgsConstructor;
import me.realimpact.dummy.swing.domain.Product.ProductTier;
import me.realimpact.dummy.swing.proxy.MobilePhoneDto;
import me.realimpact.dummy.swing.util.Util;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.proxy.OlmagoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class OlmagoCustomerServiceImpl implements OlmagoCustomerService {
  private final OlmagoCustomerRepository olmagoCustomerRepository;
  private final MobilePhoneOlmagoCustomerRelationHistoryRepository mobilePhoneOlmagoCustRelHstRepository;
  private final OlmagoProxy olmagoProxy;
  
  @Transactional
  @Override
  public void unlinkWithMobilePhoneService(MobilePhone mps, LocalDateTime unlinkDateTime) {
    olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer())
        .flatMap(oc -> mobilePhoneOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(rel -> {
          rel.terminate(unlinkDateTime);
          olmagoProxy.unlinkMobilePhoneService(
              rel.getOlmagoCustomer().getOlmagoCustId(),
              MobilePhoneDto.of(mps)
          ).subscribe();
        });
  }

  @Transactional
  @Override
  public void applyMobilePhoneLinkedDiscount(MobilePhone mps) {
    olmagoCustomerRepository.findBySwingCustomer(mps.getCustomer())
        .flatMap(oc -> mobilePhoneOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(mps, oc, Util.LocalDateTimeMax))
        .ifPresent(rel -> olmagoProxy.applyMobilePhoneLinkedDiscount(
            rel.getOlmagoCustomer().getOlmagoCustId(),
            MobilePhoneDto.of(mps)
            ).subscribe()
        );
  }
}
