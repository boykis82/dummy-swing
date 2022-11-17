package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.ChangeFeeProductRequestDto;
import me.realimpact.dummy.swing.dto.ChangeOwnerRequestDto;
import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import me.realimpact.dummy.swing.dto.TerminateRequestDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MobilePhoneServiceTest {
  @InjectMocks
  MobilePhoneServiceImpl mobilePhoneService;
  
  @Mock
  MobilePhoneRepository mobilePhoneRepository;

  @Mock
  CustomerRepository customerRepository;

  @Mock
  ProductRepository productRepository;
  
  @Mock
  OlmagoCustomerService olmagoCustomerService;

  List<MobilePhone> mobilePhones;
  List<Customer> customers;
  List<Product> products;

  @Before
  public void setUp() {
    customers = Fixtures.createManyCustomers();
    products = Fixtures.createManyProducts();
    mobilePhones = Fixtures.createManyServices(customers, products);

    customers = Fixtures.createManyCustomers();
    for (int i = 0; i < customers.size(); ++i) {
      customers.get(i).setCustNum((long)(i+1));
    }

    mobilePhones = Fixtures.createManyServices(customers, Fixtures.createManyProducts());
    for (int i = 0; i < mobilePhones.size(); ++i) {
      mobilePhones.get(i).setSvcMgmtNum((long)(i+1));
    }

  }
  
  @After
  public void tearDown() {
  }
  
  @Test
  public void givenNotExistedCI_whenGetServicesByCI_thenShouldReturnEmptyList() {
    given( mobilePhoneRepository.findByCI("77777777777777777777") )
        .willReturn( Collections.emptyList() );
    
    assertThat( mobilePhoneService.getMobilePhonesByCi("77777777777777777777") ).isEmpty();
  }
  
  @Test
  public void givenExistedCIAndNotExistedService_whenGetServicesByCI_thenShouldReturnServices() {
    mobilePhones = mobilePhones.stream()
        .filter(s -> s.getCustomer().getCi().equals("22222222222222222222"))
        .collect(Collectors.toList());

    given( mobilePhoneRepository.findByCI("22222222222222222222") )
        .willReturn(mobilePhones);

    List<MobilePhoneResponseDto> servicesResponse = mobilePhoneService.getMobilePhonesByCi("22222222222222222222");
    assertThat(servicesResponse).hasSize(1);
    assertThat(servicesResponse.get(0).getSvcNum()).isEqualTo(mobilePhones.get(0).getSvcNum());
    assertThat(servicesResponse.get(0).getFeeProdID()).isEqualTo(mobilePhones.get(0).getFeeProduct().getProdId());
    assertThat(servicesResponse.get(0).getFeeProdNm()).isEqualTo(mobilePhones.get(0).getFeeProduct().getProdNm());
    assertThat(servicesResponse.get(0).getSvcScrbDt()).isEqualTo(mobilePhones.get(0).getSvcScrbDt());
    assertThat(servicesResponse.get(0).getProductTier()).isEqualTo(mobilePhones.get(0).getFeeProduct().getProductTier().name());
  }

  @Test
  public void givenNormalStatus_whenChangeOwner_shouldChangeCustomer() {
    MobilePhone testMobilePhone = mobilePhones.get(0);
    Customer testCustomer = customers.get(5);
    given( mobilePhoneRepository.findById(1L) )
        .willReturn(Optional.of(testMobilePhone));
    given( customerRepository.findById(2L) )
        .willReturn(Optional.of(testCustomer));
    // void method는 given 사용불가. do nothing 이 default. 따라서 given 불필요

    mobilePhoneService.changeOwner(
        ChangeOwnerRequestDto.builder()
            .ownerChangeDateTime(LocalDateTime.now())
            .svcMgmtNum(1L)
            .bfCustNum(1L)
            .afCustNum(2L)
            .build()
    );

    assertThat(testMobilePhone.getCustomer()).isEqualTo(testCustomer);
  }

  @Test
  public void givenNormalStatus_whenTerminate_shouldFillSvcTermDt() {
    MobilePhone testMobilePhone = mobilePhones.get(0);
    LocalDateTime now = LocalDateTime.now();

    given( mobilePhoneRepository.findById(1L) )
        .willReturn(Optional.of(testMobilePhone));

    mobilePhoneService.terminate(
        TerminateRequestDto.builder()
            .terminatedDateTime(now)
            .svcMgmtNum(1L)
            .build()
    );

    assertThat(testMobilePhone.getSvcTermDt()).isEqualTo(now.toLocalDate());
  }

  @Test
  public void givenNormalStatus_whenChangeFeeProduct_shouldChangeProduct() {
    MobilePhone testMobilePhone = mobilePhones.get(0);
    Product afterProduct = products.get(1);

    given( mobilePhoneRepository.findById(1L) )
        .willReturn(Optional.of(testMobilePhone));
    given( productRepository.findById(afterProduct.getProdId()) )
        .willReturn(Optional.of(afterProduct));

    mobilePhoneService.changeFeeProduct(
        ChangeFeeProductRequestDto.builder()
            .svcMgmtNum(testMobilePhone.getSvcMgmtNum())
            .beforeProdId(testMobilePhone.getFeeProduct().getProdId())
            .afFeeProdId(afterProduct.getProdId())
            .build()
    );
    assertThat(testMobilePhone.getFeeProduct()).isEqualTo(afterProduct);
  }
}
