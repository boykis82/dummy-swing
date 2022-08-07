package me.realimpact.dummy.swing.domain;

import me.realimpact.dummy.swing.Fixtures;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MobilePhoneTest {
  @Autowired
  CustomerRepository customerRepository;
  
  @Autowired
  MobilePhoneRepository mobilePhoneRepository;
  
  @Autowired
  ProductRepository productRepository;

  List<Customer> customers;
  List<Product> products;
  List<MobilePhone> services;

  @Before
  public void setUp() {
    customers = customerRepository.saveAll(Fixtures.createManyCustomers());
    products = productRepository.saveAll(Fixtures.createManyProducts());
    services = mobilePhoneRepository.saveAll(Fixtures.createManyServices(customers, products));
  }
  
  @After
  public void tearDown() {
    mobilePhoneRepository.deleteAll();
    customerRepository.deleteAll();
    productRepository.deleteAll();
  }
  
  @Test
  public void findByCI_ciExistedAndserviceNotExisted_shouldBeEmpty() {
    assertThat(mobilePhoneRepository.findByCI("66666666666666666666")).isEmpty();
  }
  
  @Test
  public void findByCI_ciNotExisted_shouldBeEmpty() {
    assertThat(mobilePhoneRepository.findByCI("77777777777777777777")).isEmpty();
  }

  @Test
  public void findByCI_oneServiceExisted_shouldReturnOneService() {
    String testCi = "22222222222222222222";
    List<MobilePhone> servicesFoundByCI = mobilePhoneRepository.findByCI(testCi);
    assertThat(servicesFoundByCI).hasSize(1);
    MobilePhone foundService = servicesFoundByCI.get(0);
    assertThat(foundService.getSvcNum()).isEqualTo("3");
    assertThat(foundService.getCustomer().getCi()).isEqualTo(testCi);
    assertThat(foundService.getFeeProduct().getProdId()).isEqualTo("NA00000002");
  }

  @Test
  public void findByCI_manyServiceExisted_shouldReturnManyServices() {
    List<MobilePhone> servicesFoundByCI = mobilePhoneRepository.findByCI("33333333333333333333");
    assertThat(servicesFoundByCI).hasSize(4);
  }
}
