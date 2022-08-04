package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MobilePhoneSearchTest {
  @Autowired
  OlmagoService olmagoService;
  
  @MockBean
  MobilePhoneServiceRepository serviceRepository;
  
  List<MobilePhoneService> services;
  
  @Before
  public void setUp() {
    services = Fixtures.createManyServices(Fixtures.createManyCustomers(), Fixtures.createManyProducts());
    
    for (int i = 0; i < services.size(); ++i) {
      services.get(i).setSvcMgmtNum((long)(i+1));
    }
  }
  
  @After
  public void tearDown() {
  }
  
  @Test
  public void notExistedCI_shouldReturnEmptyList() {
    given( serviceRepository.findByCI("77777777777777777777") )
        .willReturn( Collections.emptyList() );
    
    assertThat( olmagoService.getServicesByCI("77777777777777777777") ).isEmpty();
  }
  
  @Test
  public void existedCIAndNotExistedService_shouldReturnServices() {
    services = services.stream()
        .filter(s -> s.getCustomer().getCi().equals("22222222222222222222"))
        .collect(Collectors.toList());
    
    given( serviceRepository.findByCI("22222222222222222222") )
        .willReturn(services);
    
    List<MobilePhoneResponseDto> servicesResponse = olmagoService.getServicesByCI("22222222222222222222");
    assertThat(servicesResponse).hasSize(1);
    assertThat(servicesResponse.get(0).getSvcNum()).isEqualTo("3");
    assertThat(servicesResponse.get(0).getFeeProdID()).isEqualTo("NA00000002");
    assertThat(servicesResponse.get(0).getFeeProdNm()).isEqualTo("플래티넘");
    assertThat(servicesResponse.get(0).getSvcScrbDt()).isEqualTo(LocalDate.of(2002,1,1));
    assertThat(servicesResponse.get(0).isMobilePhoneLinkedDiscountTarget()).isTrue();
  }
}
