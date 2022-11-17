package me.realimpact.dummy.swing.proxy;

import reactor.core.publisher.Mono;

public interface OlmagoProxy {
  Mono<Void> unlinkMobilePhoneService(long olmagoCustomerId, MobilePhoneDto dto);
  Mono<Void> applyMobilePhoneLinkedDiscount(long olmagoCustomerId, MobilePhoneDto dto);
}
