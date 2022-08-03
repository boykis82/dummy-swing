package me.realimpact.dummy.swing.domain;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.Util;
import me.realimpact.dummy.swing.service.OlmagoService;
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
  MobilePhoneServiceRepository serviceRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  OlmagoCustomerRepository olmagoCustomerRepository;

  @Autowired
  ServiceOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository;

  List<Customer> customers;
  List<Product> products;
  List<MobilePhoneService> services;

  OlmagoCustomer olmagoCustomer;
  OlmagoCustomer olmagoCustomer2;
  OlmagoCustomer olmagoCustomerNotMapped;
  
  ServiceOlmagoCustomerRelationHistory socrh;
  ServiceOlmagoCustomerRelationHistory socrh2;
  ServiceOlmagoCustomerRelationHistory socrh3;

  @Before
  public void setUp() {
    customers = customerRepository.saveAll(Fixtures.createManyCustomers());
    products = productRepository.saveAll(Fixtures.createManyProducts());
    services = serviceRepository.saveAll(Fixtures.createManyServices(customers, products));
    
    olmagoCustomer = olmagoCustomerRepository.save(
        OlmagoCustomer.builder().olmagoCustId(1).swingCustomer(customers.get(0)).build()
    );
    socrh = ServiceOlmagoCustomerRelationHistory.newHistory(services.get(0), olmagoCustomer, LocalDateTime.now());
    svcOlmagoCustRelHstRepository.save(socrh);
  
    olmagoCustomer2 = olmagoCustomerRepository.save(
        OlmagoCustomer.builder().olmagoCustId(2).swingCustomer(customers.get(2)).build()
    );
    socrh2 = ServiceOlmagoCustomerRelationHistory.newHistory(services.get(3), olmagoCustomer2, LocalDateTime.now());
    svcOlmagoCustRelHstRepository.save(socrh2);
  
    socrh2.terminate(LocalDateTime.now());
    socrh3 = ServiceOlmagoCustomerRelationHistory.newHistory(services.get(4), olmagoCustomer2, LocalDateTime.now());
    svcOlmagoCustRelHstRepository.save(socrh3);
  
    olmagoCustomerNotMapped = olmagoCustomerRepository.save(
        OlmagoCustomer.builder().olmagoCustId(3).swingCustomer(customers.get(3)).build()
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
    Optional<ServiceOlmagoCustomerRelationHistory> socrhOpt =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(
          services.get(0), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhOpt).isPresent();
  }

  @Test
  public void findRelation_andCondition_terminatedStatus() {
    socrh.terminate(LocalDateTime.now());
    Optional<ServiceOlmagoCustomerRelationHistory> socrhOpt =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(
            services.get(0), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhOpt).isEmpty();
  }
  
  @Test
  public void findRelation_andCondition_notExisted() {
    Optional<ServiceOlmagoCustomerRelationHistory> socrhOpt =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(
            services.get(0), olmagoCustomer2, Util.LocalDateTimeMax
        );
    assertThat(socrhOpt).isEmpty();
  }

  @Test
  public void findRelation_orCondition_matchBoth() {
    List<ServiceOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(
            services.get(0), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhs).hasSize(1);
  }
  
  @Test
  public void findRelation_orCondition_terminatedStatus() {
    socrh.terminate(LocalDateTime.now());
    List<ServiceOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(
            services.get(0), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhs).isEmpty();
  }
  
  @Test
  public void findRelation_orCondition_matchServiceOnly() {
    List<ServiceOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(
            services.get(4), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhs).isNotEmpty();
  }
  
  @Test
  public void findRelation_orCondition_matchOlmagoCustomerOnly() {
    List<ServiceOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(
            services.get(6), olmagoCustomer, Util.LocalDateTimeMax
        );
    assertThat(socrhs).isNotEmpty();
  }
  
  @Test
  public void findRelation_orCondition_notExisted() {
    List<ServiceOlmagoCustomerRelationHistory> socrhs =
        svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(
            services.get(5), olmagoCustomerNotMapped, Util.LocalDateTimeMax
        );
    assertThat(socrhs).isEmpty();
  }
}
