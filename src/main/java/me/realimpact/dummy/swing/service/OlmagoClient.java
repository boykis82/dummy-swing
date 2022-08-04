package me.realimpact.dummy.swing.service;

public interface OlmagoClient {
  void unlinkMobilePhoneService(long olmagoCustomerId, long svcMgmtNum);
  void applyMobilePhoneLinkedDiscount(long olmagoCustomerId, long svcMgmtNum);
  void cancelMobilePhoneLinkedDiscount(long olmagoCustomerId, long svcMgmtNum);
}
