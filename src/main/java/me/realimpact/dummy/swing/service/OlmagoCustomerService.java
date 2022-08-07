package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.domain.MobilePhone;

import java.time.LocalDateTime;

public interface OlmagoCustomerService {
  void unlinkWithMobilePhoneService(MobilePhone mps, LocalDateTime unlinkDateTime);
  void applyMobilePhoneLinkedDiscount(MobilePhone mps, boolean isMobilePhoneLinkedDiscountTarget);
}
