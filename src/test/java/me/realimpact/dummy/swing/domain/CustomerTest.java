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
public class CustomerTest {
  @Autowired
  CustomerRepository customerRepository;
  
  @Before
  public void setUp() {
    customerRepository.save(
      Customer.newCustomer("11111", "강인수", LocalDate.of(1982,1,1))
    );
  }
  
  @After
  public void tearDown() {
    customerRepository.deleteAll();
  }
  
  @Test
  public void findByCI_notExisted_shouldBeEmpty() {
    assertThat(customerRepository.findByCI("00000")).isEmpty();
  }
  
  @Test
  public void findByCI_existed_shouldReturnOneCustomer() {
    Optional<Customer> customer = customerRepository.findByCI("11111");
    assertThat(customer).isPresent();
    assertThat(customer.get().getCustNm()).isEqualTo("강인수");
  }
  
  @Test
  public void findByCI_inManyCustomers_shouldReturnOneCustomer() {
    customerRepository.save(
        Customer.newCustomer("22222", "황치훈", LocalDate.of(1982,11,1))
    );
    customerRepository.save(
        Customer.newCustomer("33333", "김선혁", LocalDate.of(1986,8,8))
    );
    
    Optional<Customer> customer = customerRepository.findByCI("22222");
    assertThat(customer).isPresent();
    assertThat(customer.get().getCustNm()).isEqualTo("황치훈");
  }
}
