package me.realimpact.dummy.swing.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// TODO - 김선혁이 URL 정의하면 수정

public class OlmagoProxyImpl implements OlmagoProxy {
  private final static String URL = "/customer/{olmago-customer-id}/linked-mobile-phone/{mobile-phone-svc-mgmt-num}";
  
  WebClient webClient;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  static class ApplyMobilePhoneLinkedDiscountDto {
    boolean isMobilePhoneLinkedDiscountTarget;
  }
  
  public OlmagoProxyImpl(WebClient webClient) {
    this.webClient = webClient;
  }
  
  @Override
  public Mono<Void> unlinkMobilePhoneService(long olmagoCustomerId, long svcMgmtNum) {
    return webClient.delete()
        .uri(URL, olmagoCustomerId, svcMgmtNum)
        .retrieve()
        .bodyToMono(Void.class);
  }
  
  @Override
  public Mono<Void> applyMobilePhoneLinkedDiscount(long olmagoCustomerId, long svcMgmtNum, boolean isMobilePhoneLinkedDiscountTarget) {
    return webClient.put()
        .uri(URL, olmagoCustomerId, svcMgmtNum)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(new ApplyMobilePhoneLinkedDiscountDto(isMobilePhoneLinkedDiscountTarget))
        .retrieve()
        .bodyToMono(Void.class);
  }
}
