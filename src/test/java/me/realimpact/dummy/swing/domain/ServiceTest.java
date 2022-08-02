package me.realimpact.dummy.swing.domain;

import me.realimpact.dummy.swing.Fixtures;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ServiceTest {
  @Autowired
  CustomerRepository customerRepository;
  
  @Autowired
  MobilePhoneServiceRepository serviceRepository;
  
  @Autowired
  ProductRepository productRepository;

  List<Customer> customers;
  List<Product> products;
  List<MobilePhoneService> services;

  @Before
  public void setUp() {
    customers = customerRepository.saveAll(Fixtures.createManyCustomers());
    products = productRepository.saveAll(Fixtures.createManyProducts());
    services = serviceRepository.saveAll(Fixtures.createManyServices(customers, products));
  }
  
  @After
  public void tearDown() {
    serviceRepository.deleteAll();
    customerRepository.deleteAll();
    productRepository.deleteAll();
  }
  
  @Test
  public void findByCI_serviceNotExisted() {
    assertThat(serviceRepository.findByCI("77777777777777777777")).isEmpty();
  }

  @Test
  public void findByCI_oneServiceExisted() {
    List<MobilePhoneService> servicesFoundByCI = serviceRepository.findByCI("22222222222222222222");
    assertThat(servicesFoundByCI).hasSize(1);
    MobilePhoneService foundService = servicesFoundByCI.get(0);
    assertThat(foundService.getSvcNum()).isEqualTo("3");
    assertThat(foundService.getCustomer().getCi()).isEqualTo("22222222222222222222");
    assertThat(foundService.getFeeProduct().getProdId()).isEqualTo("NA00000002");
  }

  @Test
  public void findByCI_manyServiceExisted() {
    List<MobilePhoneService> servicesFoundByCI = serviceRepository.findByCI("33333333333333333333");
    assertThat(servicesFoundByCI).hasSize(4);
  }
}
