package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.domain.Product.ProductTier;
import me.realimpact.dummy.swing.proxy.OlmagoProxy;
import me.realimpact.dummy.swing.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class OlmagoCustomerServiceTest {
  @InjectMocks
  OlmagoCustomerServiceImpl olmagoCustomerService;

  @Mock
  OlmagoCustomerRepository olmagoCustomerRepository;

  @Mock
  MobilePhoneOlmagoCustomerRelationHistoryRepository mpocrhRepository;

  @Mock
  OlmagoProxy olmagoProxy;

  List<MobilePhone> services;
  List<Customer> customers;
  List<OlmagoCustomer> olmagoCustomers;

  MobilePhone testSvc;
  OlmagoCustomer testOlmagoCust;
  OlmagoCustomer testOlmagoCust2;
  MobilePhoneOlmagoCustomerRelationHistory testSocrh;

  public OlmagoCustomerServiceTest() {
    customers = Fixtures.createManyCustomers();
    for (int i = 0; i < customers.size(); ++i) {
      customers.get(i).setCustNum((long)(i+1));
    }

    services = Fixtures.createManyServices(customers, Fixtures.createManyProducts());
    for (int i = 0; i < services.size(); ++i) {
      services.get(i).setSvcMgmtNum((long)(i+1));
    }

    olmagoCustomers = List.of(
        OlmagoCustomer.builder().olmagoCustId(2L).swingCustomer(services.get(0).getCustomer()).build(),
        OlmagoCustomer.builder().olmagoCustId(3L).swingCustomer(services.get(2).getCustomer()).build()
    );

    testSvc = services.get(0);
    testOlmagoCust = olmagoCustomers.get(0);
    testOlmagoCust2 = olmagoCustomers.get(1);
    testSocrh = MobilePhoneOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, LocalDateTime.now());
  }

  @Test
  public void givenNotExistedOlmagoCustomer_whenUnlinkWithMobilePhoneService_thenDoNothing() {
    given( olmagoCustomerRepository.findBySwingCustomer(any(Customer.class)) )
        .willReturn(Optional.empty());

    olmagoCustomerService.unlinkWithMobilePhoneService(testSvc, LocalDateTime.now());

    assertThat(true).isTrue();
  }

  @Test
  public void givenExistedOlmagoCustomerButNotExistedMobilePhoneRelation_whenUnlinkWithMobilePhoneService_thenDoNothing() {
    given( olmagoCustomerRepository.findBySwingCustomer(any(Customer.class)) )
        .willReturn(Optional.of(testOlmagoCust));
    given( mpocrhRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(testSvc, testOlmagoCust, Util.LocalDateTimeMax) )
        .willReturn(Optional.empty());

    olmagoCustomerService.unlinkWithMobilePhoneService(testSvc, LocalDateTime.now());

    assertThat(true).isTrue();
  }

  @Test
  public void givenExistedMobilePhoneRelation_whenUnlinkWithMobilePhoneService_thenShouldTerminateHistory() {
    LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
    LocalDateTime now = LocalDateTime.now();
    MobilePhoneOlmagoCustomerRelationHistory mpocrh = MobilePhoneOlmagoCustomerRelationHistory.newHistory(
        testSvc, testOlmagoCust, fiveDaysAgo
    );

    given( olmagoCustomerRepository.findBySwingCustomer(any(Customer.class)) )
        .willReturn(Optional.of(testOlmagoCust));
    given( mpocrhRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(testSvc, testOlmagoCust, Util.LocalDateTimeMax) )
        .willReturn(Optional.of(mpocrh));
    given( olmagoProxy.unlinkMobilePhoneService(testOlmagoCust.getOlmagoCustId(), testSvc.getSvcMgmtNum()) )
        .willReturn(Mono.empty());

    assertThat(mpocrh.getEffStaDtm()).isEqualTo(fiveDaysAgo);
    assertThat(mpocrh.getEffEndDtm()).isEqualTo(Util.LocalDateTimeMax);

    olmagoCustomerService.unlinkWithMobilePhoneService(testSvc, now);

    assertThat(mpocrh.getEffStaDtm()).isEqualTo(fiveDaysAgo);
    assertThat(mpocrh.getEffEndDtm()).isEqualTo(now);
  }

  @Test
  public void givenNotExistedOlmagoCustomer_whenApplyMobilePhoneLinkedDiscount_thenDoNothing() {
    given( olmagoCustomerRepository.findBySwingCustomer(any(Customer.class)) )
        .willReturn(Optional.empty());

    olmagoCustomerService.applyMobilePhoneLinkedDiscount(testSvc, ProductTier.HIGHEST);

    assertThat(true).isTrue();
  }

  @Test
  public void givenExistedOlmagoCustomerButNotExistedMobilePhoneRelation_whenApplyMobilePhoneLinkedDiscount_thenDoNothing() {
    given( olmagoCustomerRepository.findBySwingCustomer(any(Customer.class)) )
        .willReturn(Optional.of(testOlmagoCust));
    given( mpocrhRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(testSvc, testOlmagoCust, Util.LocalDateTimeMax) )
        .willReturn(Optional.empty());

    olmagoCustomerService.applyMobilePhoneLinkedDiscount(testSvc, ProductTier.HIGHEST);

    assertThat(true).isTrue();
  }

  @Test
  public void givenExistedMobilePhoneRelation_whenApplyMobilePhoneLinkedDiscount_thenDoNothing() {
    MobilePhoneOlmagoCustomerRelationHistory mpocrh = MobilePhoneOlmagoCustomerRelationHistory.newHistory(
        testSvc, testOlmagoCust, LocalDateTime.now()
    );

    given( olmagoCustomerRepository.findBySwingCustomer(any(Customer.class)) )
        .willReturn(Optional.of(testOlmagoCust));
    given( mpocrhRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(testSvc, testOlmagoCust, Util.LocalDateTimeMax) )
        .willReturn(Optional.of(mpocrh));
    given( olmagoProxy.applyMobilePhoneLinkedDiscount(testOlmagoCust.getOlmagoCustId(), testSvc.getSvcMgmtNum(), ProductTier.HIGHEST) )
        .willReturn(Mono.empty());

    olmagoCustomerService.applyMobilePhoneLinkedDiscount(testSvc, ProductTier.HIGHEST);

    assertThat(true).isTrue();
  }
}
