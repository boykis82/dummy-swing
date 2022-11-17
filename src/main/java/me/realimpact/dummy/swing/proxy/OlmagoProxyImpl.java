package me.realimpact.dummy.swing.proxy;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class OlmagoProxyImpl implements OlmagoProxy {
  private final WebClient webClient;

  @Override
  public Mono<Void> unlinkMobilePhoneService(long olmagoCustomerId, MobilePhoneDto dto) {
    return webClient.put()
        .uri(uriBuilder -> uriBuilder.path("/customers/{olmago-customer-id}/unlinkMobilePhone").build(olmagoCustomerId))
        .body(BodyInserters.fromValue(dto))
        .retrieve()
        .bodyToMono(Void.class);
  }
  
  @Override
  public Mono<Void> applyMobilePhoneLinkedDiscount(long olmagoCustomerId, MobilePhoneDto dto) {
    return webClient.put()
        .uri(uriBuilder -> uriBuilder.path("/customers/{olmago-customer-id}/changeMobilePhonePricePlan").build(olmagoCustomerId))
        .body(BodyInserters.fromValue(dto))
        .retrieve()
        .bodyToMono(Void.class);
  }
}
