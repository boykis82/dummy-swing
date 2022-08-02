package me.realimpact.dummy.swing.domain;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OlmagoCustomerTest {
  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  OlmagoCustomerRepository olmagoCustomerRepository;

  Customer swingCustomer1;
  Customer swingCustomer2;

  @Before
  public void setUp() {
    swingCustomer1 = customerRepository.save(
        Customer.register("11111", "강인수", LocalDate.of(1982,1,1))
    );
    olmagoCustomerRepository.save(
        OlmagoCustomer.builder().olmagoCustId(5).swingCustomer(swingCustomer1).build()
    );

    swingCustomer2 = customerRepository.save(
        Customer.register("22222", "김범수", LocalDate.of(1996,1,1))
    );
  }

  @After
  public void tearDown() {
    olmagoCustomerRepository.deleteAll();
    customerRepository.deleteAll();
  }

  @Test
  public void findByCustomer_notMappedWithOlmagoCustomer_shouldReturnNull() {
    assertThat(olmagoCustomerRepository.findBySwingCustomer(swingCustomer2)).isEmpty();
  }

  @Test
  public void findByCustomer_mappedWithOlmagoCustomer_shouldReturn() {
    Optional<OlmagoCustomer> olmagoCustomerOpt =
        olmagoCustomerRepository.findBySwingCustomer(swingCustomer1);
    assertThat(olmagoCustomerOpt).isPresent();
    assertThat(olmagoCustomerOpt.get().getOlmagoCustId()).isEqualTo(5);
  }
}
