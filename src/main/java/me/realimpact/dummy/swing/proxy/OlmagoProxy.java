package me.realimpact.dummy.swing.proxy;

import reactor.core.publisher.Mono;

public interface OlmagoProxy {
  Mono<Void> unlinkMobilePhoneService(long olmagoCustomerId, long svcMgmtNum);
  Mono<Void> applyMobilePhoneLinkedDiscount(long olmagoCustomerId, long svcMgmtNum, boolean isMobilePhoneLinkedDiscountTarget);
}
