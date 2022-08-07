package me.realimpact.dummy.swing.domain;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.util.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ServiceOlmagoCustomerRelationTest {
  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  MobilePhoneRepository serviceRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  OlmagoCustomerRepository olmagoCustomerRepository;

  @Autowired
  MobilePhoneOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository;

  List<Customer> customers;
  List<Product> products;
  List<MobilePhone> services;

  OlmagoCustomer olmagoCustomer;
  OlmagoCustomer olmagoCustomer2;
  OlmagoCustomer olmagoCustomerNotMapped;
  
  MobilePhoneOlmagoCustomerRelationHistory socrh;
  MobilePhoneOlmagoCustomerRelationHistory socrh2;
  MobilePhoneOlmagoCustomerRelationHistory socrh3;

  @Before
  public void setUp() {
    customers = customerRepository.saveAll(Fixtures.createManyCustomers());
    products = productRepository.saveAll(Fixtures.createManyProducts());
    services = serviceRepository.saveAll(Fixtures.createManyServices(customers, products));
    
    olmagoCustomer = olmagoCustomerRepository.save(
        OlmagoCustomer.builder().olmagoCustId(1L).swingCustomer(customers.get(0)).build()
    );
    socrh = MobilePhoneOlmagoCustomerRelationHistory.newHistory(services.get(0), olmagoCustomer, LocalDateTime.now());
    svcOlmagoCustRelHstRepository.save(socrh);
  
    olmagoCustomer2 = olmagoCustomerRepository.save(
        OlmagoCustomer.builder().olmagoCustId(2L).swingCustomer(customers.get(2)).build()
    );
    socrh2 = MobilePhoneOlmagoCustomerRelationHistory.newHistory(services.get(3), olmagoCustomer2, LocalDateTime.now());
    svcOlmagoCustRelHstRepository.save(socrh2);
  
    socrh2.terminate(LocalDateTime.now());
    socrh3 = MobilePhoneOlmagoCustomerRelationHistory.newHistory(services.get(4), olmagoCustomer2, LocalDateTime.now());
    svcOlmagoCustRelHstRepository.save(socrh3);
  
    olmagoCustomerNotMapped = olmagoCustomerRepository.save(
        OlmagoCustomer.builder().olmagoCustId(3L).swingCustomer(customers.get(3)).build()
    );
  }

  @After
  public void tearDown() {
    svcOlmagoCustRelHstRepository.deleteAll();
    olmagoCustomerRepository.deleteAll();
    serviceRepository.deleteAll();
    customerRepository.deleteAll();
    productRepository.deleteAll();
  }

  @Test
  public void makeNewRelation() {
    assertThat(socrh.getId()).isNotEqualTo(0);
    assertThat(socrh.isActiveHistory()).isTrue();

    socrh.terminate(LocalDateTime.now());
    assertThat(socrh.terminated()).isTrue();
  }

  @Test
  public void findRelation_andCondition_activeStatus() {
    Optional<MobilePhoneOlmagoCustomerRelationHistory> socrhOpt =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(
          services.get(0), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhOpt).isPresent();
  }

  @Test
  public void findRelation_andCondition_terminatedStatus() {
    socrh.terminate(LocalDateTime.now());
    Optional<MobilePhoneOlmagoCustomerRelationHistory> socrhOpt =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(
            services.get(0), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhOpt).isEmpty();
  }
  
  @Test
  public void findRelation_andCondition_notExisted() {
    Optional<MobilePhoneOlmagoCustomerRelationHistory> socrhOpt =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(
            services.get(0), olmagoCustomer2, Util.LocalDateTimeMax
        );
    assertThat(socrhOpt).isEmpty();
  }

  @Test
  public void findRelation_orCondition_matchBoth() {
    List<MobilePhoneOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(
            services.get(0), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhs).hasSize(1);
  }
  
  @Test
  public void findRelation_orCondition_terminatedStatus() {
    socrh.terminate(LocalDateTime.now());
    List<MobilePhoneOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(
            services.get(0), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhs).isEmpty();
  }
  
  @Test
  public void findRelation_orCondition_matchServiceOnly() {
    List<MobilePhoneOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(
            services.get(4), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhs).isNotEmpty();
  }
  
  @Test
  public void findRelation_orCondition_matchOlmagoCustomerOnly() {
    List<MobilePhoneOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(
            services.get(6), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhs).isNotEmpty();
  }
  
  @Test
  public void findRelation_orCondition_notExisted() {
    List<MobilePhoneOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(
            services.get(5), olmagoCustomerNotMapped, Util.LocalDateTimeMax
        );
    assertThat(socrhs).isEmpty();
  }
}
