package me.realimpact.dummy.swing.domain;

import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

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
  
  @Before
  public void setUp() {
    customerRepository.save(
        Customer.register("11111", "강인수", LocalDate.of(1982,1,1))
    );
    productRepository.save(
        Product.builder().prodId("NA00000001").prodNm("라지").isMobilePhoneLinkedDiscountTarget(true).build()
    );
  }
  
  @After
  public void tearDown() {
    serviceRepository.deleteAll();
    customerRepository.deleteAll();
    productRepository.deleteAll();
  }
  
  @Test
  public void subscribe() {
    Customer customer = customerRepository.findByCI("11111").get();
    Product product = productRepository.findById("NA00000001").get();
  
    serviceRepository.save(
        MobilePhoneService.subscribe(customer, "1", LocalDate.now(), product)
    );
    
    assertThat(
        serviceRepository.findById(1L).isPresent()
    ).isTrue();
  }
}
