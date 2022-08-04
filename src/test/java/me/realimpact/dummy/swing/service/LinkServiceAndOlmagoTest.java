package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.ReqRelSvcAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.SvcAndOlmagoRelationResponseDto;
import me.realimpact.dummy.swing.exception.BusinessException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LinkServiceAndOlmagoTest {
  @Autowired
  OlmagoService olmagoService;
  
  @MockBean
  OlmagoCustomerRepository olmagoCustomerRepository;
  
  @MockBean
  ServiceOlmagoCustomerRelationHistoryRepository svcOlmagoCustRelHstRepository;
  
  @MockBean
  MobilePhoneServiceRepository serviceRepository;
  
  List<MobilePhoneService> services;
  List<Customer> customers;
  List<OlmagoCustomer> olmagoCustomers;
  
  MobilePhoneService testSvc;
  OlmagoCustomer testOlmagoCust;
  OlmagoCustomer testOlmagoCust2;
  ServiceOlmagoCustomerRelationHistory testSocrh;
  
  public LinkServiceAndOlmagoTest() {
    customers = Fixtures.createManyCustomers();
    for (int i = 0; i < customers.size(); ++i) {
      customers.get(i).setCustNum((long)(i+1));
    }
    
    services = Fixtures.createManyServices(customers, Fixtures.createManyProducts());
    for (int i = 0; i < services.size(); ++i) {
      services.get(i).setSvcMgmtNum((long)(i+1));
    }

    olmagoCustomers = List.of(
        OlmagoCustomer.builder().olmagoCustId(2L).swingCustomer(services.get(0).getCustomer()).build(),
        OlmagoCustomer.builder().olmagoCustId(3L).swingCustomer(services.get(2).getCustomer()).build()
    );
  
    testSvc = services.get(0);
    testOlmagoCust = olmagoCustomers.get(0);
    testOlmagoCust2 = olmagoCustomers.get(1);
    testSocrh = ServiceOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, LocalDateTime.now());
  }
  
  @Before
  public void setUp() {

  }
  
  @After
  public void tearDown() {
  }
  
  /* TODO
  1. 이상한 서비스관리번호로 연결 시도 => 오류 (ok)
  2. 얼마고고객 미존재 상태에서 연결 시도 -> 얼마고고객 생성됨. 서비스의 명의고객 기반으로 생성되므로 고객 불일치 상황은 없음
     2-2. 기존 릴레이션 미존재 -> 정상 (ok)
  3. 얼마고고객 존재 상태에서 연결 시도
     3-1. 고객 다르면 -> 오류
     3-2. 기존 릴레이션(서비스 또는 얼마고고객) 존재 -> 오류
     3-3. 기존 릴레이션 미존재 -> 정상
   */
  
  @Test(expected = BusinessException.class)
  public void link_notExistedService_shouldThrowException() {
    given( serviceRepository.findById(1L) )
        .willReturn( Optional.empty() );
  
    ReqRelSvcAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    olmagoService.linkOlmagoCustomerWithMobilePhoneService(dto);
    fail("앞에서 exception 발생해야 함");
  }

  @Test
  public void link_olmagoCustomerNotExisted_relationNotExisted_shouldBeOk() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.empty() );
    given( olmagoCustomerRepository.save(any(OlmagoCustomer.class)) )
        .willReturn(testOlmagoCust);
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(eq(testSvc), eq(testOlmagoCust), any(LocalDateTime.class)) )
        .willReturn( Collections.emptyList() );
    given( svcOlmagoCustRelHstRepository.save(any(ServiceOlmagoCustomerRelationHistory.class)) )
        .willReturn(testSocrh);
  
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId());
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(testSvc.getSvcMgmtNum());
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(testOlmagoCust.getOlmagoCustId());
  }
  
  @Test(expected = BusinessException.class)
  public void link_olmagoCustomerExisted_relationExisted_sameCustomer_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(eq(testSvc), eq(testOlmagoCust), any(LocalDateTime.class)) )
        .willReturn( Collections.singletonList(testSocrh) );
  
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId());
    olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test
  public void link_olmagoCustomerExisted_relationNotExisted_sameCustomer_shouldBeOk() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(eq(testSvc), eq(testOlmagoCust), any(LocalDateTime.class)) )
        .willReturn( Collections.emptyList() );
    given( svcOlmagoCustRelHstRepository.save(any(ServiceOlmagoCustomerRelationHistory.class)) )
        .willReturn(testSocrh);
  
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId());
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(testSvc.getSvcMgmtNum());
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(testOlmagoCust.getOlmagoCustId());
  }
  
  @Test(expected = BusinessException.class)
  public void link_olmagoCustomerExisted_diffCustomer_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust2.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust2) );
  
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust2.getOlmagoCustId());
    olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    fail("앞에서 exception 발생해야 함");
  }
  
  /* TODO
  1. 이상한 서비스관리번호로 연결 시도 => 오류
  2. 이상한 얼마고고객으로 연결 시도 => 오류
  3. 기존 릴레이션(서비스 또는 얼마고고객) 미존재 -> 오류
  4. 기존 릴레이션 존재 -> 정상
   */
  @Test(expected = BusinessException.class)
  public void unlink_notExistedService_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.empty() );
  
    ReqRelSvcAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), 2L);
    olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test(expected = BusinessException.class)
  public void unlink_notExistedOlmagoCustomer_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.empty() );
  
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId());
    olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(reqDto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test(expected = BusinessException.class)
  public void unlink_notExistedRelation_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust2.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust2) );
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(eq(testSvc), eq(testOlmagoCust2), any(LocalDateTime.class)) )
        .willReturn( Optional.empty() );
  
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust2.getOlmagoCustId());
    olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(reqDto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test
  public void unlink_existedCorrectRelation_shouldBeOk() {
    LocalDateTime startDateTime = LocalDateTime.now().minusSeconds(10);
    LocalDateTime endDateTime = LocalDateTime.now();
    ServiceOlmagoCustomerRelationHistory testSocrh = ServiceOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, startDateTime);
    
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(eq(testSvc), eq(testOlmagoCust), any(LocalDateTime.class)) )
        .willReturn( Optional.of(testSocrh) );
    given( svcOlmagoCustRelHstRepository.save(testSocrh) )
        .willReturn(testSocrh);
  
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId(), endDateTime);
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(reqDto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(testSvc.getSvcMgmtNum());
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(testOlmagoCust.getOlmagoCustId());
    assertThat(resDto.getEventDataTime()).isEqualTo(endDateTime);
  }
  
  private ReqRelSvcAndOlmagoCustDto buildReqRelSvcAndOlmagoCustDto(long svcMgmtNum, long olmagoCustomerId) {
    return buildReqRelSvcAndOlmagoCustDto(svcMgmtNum, olmagoCustomerId, LocalDateTime.now());
  }
  
  private ReqRelSvcAndOlmagoCustDto buildReqRelSvcAndOlmagoCustDto(long svcMgmtNum, long olmagoCustomerId, LocalDateTime eventDateTime) {
    return ReqRelSvcAndOlmagoCustDto.builder()
        .svcMgmtNum(svcMgmtNum)
        .olmagoCustomerId(olmagoCustomerId)
        .eventDateTime(eventDateTime)
        .build();
  }
  
}
