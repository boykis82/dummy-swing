package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.Util;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.ReqRelSvcAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.SvcAndOlmagoRelationResponseDto;
import me.realimpact.dummy.swing.exception.BusinessException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
  
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();
  
  public LinkServiceAndOlmagoTest() {
    customers = Fixtures.createManyCustomers();
    services = Fixtures.createManyServices(customers, Fixtures.createManyProducts());
  
    for (int i = 0; i < services.size(); ++i) {
      services.get(i).setSvcMgmtNum((long)(i+1));
    }
  
    for (int i = 0; i < customers.size(); ++i) {
      customers.get(i).setCustNum((long)(i+1));
    }
  
    olmagoCustomers = List.of(
        OlmagoCustomer.builder().olmagoCustId(2L).swingCustomer(services.get(0).getCustomer()).build(),
        OlmagoCustomer.builder().olmagoCustId(3L).swingCustomer(services.get(2).getCustomer()).build()
    );
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
     2-1. 기존 릴레이션(서비스) 존재 -> 오류 (ok)
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
  
  @Test(expected = BusinessException.class)
  public void link_olmagoCustomerNotExisted_relationExisted_shouldThrowException() {
    MobilePhoneService testSvc = services.get(0);
    OlmagoCustomer testOlmagoCust = olmagoCustomers.get(0);
    ServiceOlmagoCustomerRelationHistory testSocrh = ServiceOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, LocalDateTime.now());
    
    given( serviceRepository.findById(any()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(any()) )
        .willReturn( Optional.empty() );
    given( olmagoCustomerRepository.save(any()) )
        .willReturn(testOlmagoCust);
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(any(), any(), any()) )
        .willReturn( Collections.singletonList(testSocrh) );
    
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test
  public void link_olmagoCustomerNotExisted_relationNotExisted_shouldBeOk() {
    MobilePhoneService testSvc = services.get(0);
    OlmagoCustomer testOlmagoCust = olmagoCustomers.get(0);
    ServiceOlmagoCustomerRelationHistory testSocrh = ServiceOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, LocalDateTime.now());
    
    given( serviceRepository.findById(any()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(any()) )
        .willReturn( Optional.empty() );
    given( olmagoCustomerRepository.save(any()) )
        .willReturn(testOlmagoCust);
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(any(), any(), any()) )
        .willReturn( Collections.emptyList() );
    given( svcOlmagoCustRelHstRepository.save(any()) )
        .willReturn(testSocrh);
  
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(1L);
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(2L);
  }
  
  @Test(expected = BusinessException.class)
  public void link_olmagoCustomerExisted_relationExisted_sameCustomer_shouldThrowException() {
    MobilePhoneService testSvc = services.get(0);
    OlmagoCustomer testOlmagoCust = olmagoCustomers.get(0);
    ServiceOlmagoCustomerRelationHistory testSocrh = ServiceOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, LocalDateTime.now());
    
    given( serviceRepository.findById(any()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(any()) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(any(), any(), any()) )
        .willReturn( Collections.singletonList(testSocrh) );
    
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test
  public void link_olmagoCustomerExisted_relationNotExisted_sameCustomer_shouldBeOk() {
    MobilePhoneService testSvc = services.get(0);
    OlmagoCustomer testOlmagoCust = olmagoCustomers.get(0);
    ServiceOlmagoCustomerRelationHistory testSocrh = ServiceOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, LocalDateTime.now());
    
    given( serviceRepository.findById(any()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(any()) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceOrOlmagoCustomer(any(), any(), any()) )
        .willReturn( Collections.emptyList() );
    given( svcOlmagoCustRelHstRepository.save(any()) )
        .willReturn(testSocrh);
    
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(1L);
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(2L);
  }
  
  @Test(expected = BusinessException.class)
  public void link_olmagoCustomerExisted_diffCustomer_shouldThrowException() {
    MobilePhoneService testSvc = services.get(0);
    OlmagoCustomer testOlmagoCust = olmagoCustomers.get(1);
    
    given( serviceRepository.findById(any()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(any()) )
        .willReturn( Optional.of(testOlmagoCust) );
    
    ReqRelSvcAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(1L, 3L);
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
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
    given( serviceRepository.findById(1L) )
        .willReturn( Optional.empty() );
  
    ReqRelSvcAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test(expected = BusinessException.class)
  public void unlink_notExistedOlmagoCustomer_shouldThrowException() {
    MobilePhoneService testSvc = services.get(0);
  
    given( serviceRepository.findById(1L) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(2L) )
        .willReturn( Optional.empty() );
    
    ReqRelSvcAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test(expected = BusinessException.class)
  public void unlink_notExistedRelation_shouldThrowException() {
    MobilePhoneService testSvc = services.get(0);
    OlmagoCustomer testOlmagoCust = olmagoCustomers.get(1);
    
    given( serviceRepository.findById(1L) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(2L) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(any(), any(), any()) )
        .willReturn( Optional.empty() );
    
    ReqRelSvcAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto);
    fail("앞에서 exception 발생해야 함");
  }
  
  @Test
  public void unlink_existedCorrectRelation_shouldBeOk() {
    MobilePhoneService testSvc = services.get(0);
    OlmagoCustomer testOlmagoCust = olmagoCustomers.get(0);
    LocalDateTime startDateTime = LocalDateTime.now().minusSeconds(10);
    LocalDateTime endDateTime = LocalDateTime.now();
    ServiceOlmagoCustomerRelationHistory testSocrh = ServiceOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, startDateTime);
    
    given( serviceRepository.findById(1L) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(2L) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( svcOlmagoCustRelHstRepository.findRelationHistoryByServiceAndOlmagoCustomer(testSvc, testOlmagoCust, Util.LocalDateTimeMax) )
        .willReturn( Optional.of(testSocrh) );
    given( svcOlmagoCustRelHstRepository.save(testSocrh) )
        .willReturn(testSocrh);
    
    ReqRelSvcAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L, endDateTime);
    SvcAndOlmagoRelationResponseDto resDto = olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(1L);
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(2L);
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
