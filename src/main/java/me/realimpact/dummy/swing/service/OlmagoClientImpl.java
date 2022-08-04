package me.realimpact.dummy.swing.service;

import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OlmagoClientImpl implements OlmagoClient {
  RestTemplate restTemplate = new RestTemplate();
  
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
