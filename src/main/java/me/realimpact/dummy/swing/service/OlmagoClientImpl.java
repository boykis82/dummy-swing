package me.realimpact.dummy.swing.service;

import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class OlmagoClientImpl implements OlmagoClient {
 
  
  @Override
  public void unlinkMobilePhoneService(long olmagoCustomerId, long svcMgmtNum) {
    // todo
  }
  
  @Override
  public void applyMobilePhoneLinkedDiscount(long olmagoCustomerId, long svcMgmtNum) {
    // todo
  }
  
  @Override
  public void cancelMobilePhoneLinkedDiscount(long olmagoCustomerId, long svcMgmtNum) {
    // todo
  }
}
