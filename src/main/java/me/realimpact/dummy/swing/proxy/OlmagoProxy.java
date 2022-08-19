package me.realimpact.dummy.swing.proxy;

import me.realimpact.dummy.swing.domain.Product;
import reactor.core.publisher.Mono;

public interface OlmagoProxy {
  Mono<Void> unlinkMobilePhoneService(long olmagoCustomerId, long svcMgmtNum);
  Mono<Void> applyMobilePhoneLinkedDiscount(long olmagoCustomerId, long svcMgmtNum, Product.ProductTier productTier);
}
