package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.domain.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MobilePhoneSearchTest {
  @Autowired
  OlmagoService olmagoService;
  
  @Autowired
  CustomerRepository customerRepository;
  
  @Autowired
  ProductRepository productRepository;
  
  @Autowired
  MobilePhoneServiceRepository serviceRepository;
  
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
    productRepository.deleteAll();
    customerRepository.deleteAll();
  }
  
  @Test
  public void notExistedCIShouldReturnEmptyList() {
    assertThat(
        olmagoService.getServicesByCI("77777777777777777777").size()
    ).isZero();
  }
}
