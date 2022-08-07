package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.dto.ReqRelMobilePhoneAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.MobilePhoneAndOlmagoRelationResponseDto;
import me.realimpact.dummy.swing.exception.BusinessException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LinkServiceAndOlmagoTest {
  @InjectMocks
  OlmagoServiceImpl olmagoService;
  
  @Mock
  OlmagoCustomerRepository olmagoCustomerRepository;
  
  @Mock
  MobilePhoneOlmagoCustomerRelationHistoryRepository mpocrhRepository;
  
  @Mock
  MobilePhoneRepository serviceRepository;
  
  List<MobilePhone> mobilePhones;
  List<Customer> customers;
  List<OlmagoCustomer> olmagoCustomers;
  
  MobilePhone testSvc;
  OlmagoCustomer testOlmagoCust;
  OlmagoCustomer testOlmagoCust2;
  MobilePhoneOlmagoCustomerRelationHistory testSocrh;
  
  public LinkServiceAndOlmagoTest() {
    customers = Fixtures.createManyCustomers();
    for (int i = 0; i < customers.size(); ++i) {
      customers.get(i).setCustNum((long)(i+1));
    }
    
    mobilePhones = Fixtures.createManyServices(customers, Fixtures.createManyProducts());
    for (int i = 0; i < mobilePhones.size(); ++i) {
      mobilePhones.get(i).setSvcMgmtNum((long)(i+1));
    }

    olmagoCustomers = List.of(
        OlmagoCustomer.builder().olmagoCustId(2L).swingCustomer(mobilePhones.get(0).getCustomer()).build(),
        OlmagoCustomer.builder().olmagoCustId(3L).swingCustomer(mobilePhones.get(2).getCustomer()).build()
    );
  
    testSvc = mobilePhones.get(0);
    testOlmagoCust = olmagoCustomers.get(0);
    testOlmagoCust2 = olmagoCustomers.get(1);
    testSocrh = MobilePhoneOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, LocalDateTime.now());
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
  
  @Test
  public void link_notExistedService_shouldThrowException() {
    given( serviceRepository.findById(1L) )
        .willReturn( Optional.empty() );
  
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L);
    
    try {
      olmagoService.linkOlmagoCustomerWithMobilePhoneService(dto);
      fail("앞에서 exception 발생해야 함");
    } catch (BusinessException e) {
      assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  public void link_olmagoCustomerNotExisted_relationNotExisted_shouldBeOk() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.empty() );
    given( olmagoCustomerRepository.save(any(OlmagoCustomer.class)) )
        .willReturn(testOlmagoCust);
    given( mpocrhRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(eq(testSvc), eq(testOlmagoCust), any(LocalDateTime.class)) )
        .willReturn( Collections.emptyList() );
    given( mpocrhRepository.save(any(MobilePhoneOlmagoCustomerRelationHistory.class)) )
        .willReturn(testSocrh);
  
    ReqRelMobilePhoneAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId());
    MobilePhoneAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(testSvc.getSvcMgmtNum());
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(testOlmagoCust.getOlmagoCustId());
  }
  
  @Test
  public void link_olmagoCustomerExisted_relationExisted_sameCustomer_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( mpocrhRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(eq(testSvc), eq(testOlmagoCust), any(LocalDateTime.class)) )
        .willReturn( Collections.singletonList(testSocrh) );
  
    ReqRelMobilePhoneAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId());
    try {
      olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
      fail("앞에서 exception 발생해야 함");
    } catch (BusinessException e) {
      assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
  }
  
  @Test
  public void link_olmagoCustomerExisted_relationNotExisted_sameCustomer_shouldBeOk() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( mpocrhRepository.findRelationHistoryByMobilePhoneOrOlmagoCustomer(eq(testSvc), eq(testOlmagoCust), any(LocalDateTime.class)) )
        .willReturn( Collections.emptyList() );
    given( mpocrhRepository.save(any(MobilePhoneOlmagoCustomerRelationHistory.class)) )
        .willReturn(testSocrh);
  
    ReqRelMobilePhoneAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId());
    MobilePhoneAndOlmagoRelationResponseDto resDto = olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(testSvc.getSvcMgmtNum());
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(testOlmagoCust.getOlmagoCustId());
  }
  
  @Test
  public void link_olmagoCustomerExisted_diffCustomer_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust2.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust2) );
  
    ReqRelMobilePhoneAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust2.getOlmagoCustId());
    try {
      olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqDto);
      fail("앞에서 exception 발생해야 함");
    } catch (BusinessException e) {
      assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
    }
  }
  
  /* TODO
  1. 이상한 서비스관리번호로 연결 시도 => 오류
  2. 이상한 얼마고고객으로 연결 시도 => 오류
  3. 기존 릴레이션(서비스 또는 얼마고고객) 미존재 -> 오류
  4. 기존 릴레이션 존재 -> 정상
   */
  @Test
  public void unlink_notExistedService_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.empty() );
  
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), 2L);
    try {
      olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto);
      fail("앞에서 exception 발생해야 함");
    } catch (BusinessException e) {
      assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }
  
  @Test
  public void unlink_notExistedOlmagoCustomer_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.empty() );
  
    ReqRelMobilePhoneAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId());
    try {
      olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(reqDto);
      fail("앞에서 exception 발생해야 함");
    } catch (BusinessException e) {
      assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }
  
  @Test
  public void unlink_notExistedRelation_shouldThrowException() {
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust2.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust2) );
    given( mpocrhRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(eq(testSvc), eq(testOlmagoCust2), any(LocalDateTime.class)) )
        .willReturn( Optional.empty() );
  
    ReqRelMobilePhoneAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust2.getOlmagoCustId());
    try {
      olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(reqDto);
      fail("앞에서 exception 발생해야 함");
    } catch (BusinessException e) {
      assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
  }
  
  @Test
  public void unlink_existedCorrectRelation_shouldBeOk() {
    LocalDateTime startDateTime = LocalDateTime.now().minusSeconds(10);
    LocalDateTime endDateTime = LocalDateTime.now();
    MobilePhoneOlmagoCustomerRelationHistory testSocrh = MobilePhoneOlmagoCustomerRelationHistory.newHistory(testSvc, testOlmagoCust, startDateTime);
    
    given( serviceRepository.findById(testSvc.getSvcMgmtNum()) )
        .willReturn( Optional.of(testSvc) );
    given( olmagoCustomerRepository.findById(testOlmagoCust.getOlmagoCustId()) )
        .willReturn( Optional.of(testOlmagoCust) );
    given( mpocrhRepository.findRelationHistoryByMobilePhoneAndOlmagoCustomer(eq(testSvc), eq(testOlmagoCust), any(LocalDateTime.class)) )
        .willReturn( Optional.of(testSocrh) );
    given( mpocrhRepository.save(testSocrh) )
        .willReturn(testSocrh);
  
    ReqRelMobilePhoneAndOlmagoCustDto reqDto = buildReqRelSvcAndOlmagoCustDto(testSvc.getSvcMgmtNum(), testOlmagoCust.getOlmagoCustId(), endDateTime);
    MobilePhoneAndOlmagoRelationResponseDto resDto = olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(reqDto);
    assertThat(resDto.getSvcMgmtNum()).isEqualTo(testSvc.getSvcMgmtNum());
    assertThat(resDto.getOlmagoCustomerId()).isEqualTo(testOlmagoCust.getOlmagoCustId());
    assertThat(resDto.getEventDataTime()).isEqualTo(endDateTime);
  }
  
  private ReqRelMobilePhoneAndOlmagoCustDto buildReqRelSvcAndOlmagoCustDto(long svcMgmtNum, long olmagoCustomerId) {
    return buildReqRelSvcAndOlmagoCustDto(svcMgmtNum, olmagoCustomerId, LocalDateTime.now());
  }
  
  private ReqRelMobilePhoneAndOlmagoCustDto buildReqRelSvcAndOlmagoCustDto(long svcMgmtNum, long olmagoCustomerId, LocalDateTime eventDateTime) {
    return ReqRelMobilePhoneAndOlmagoCustDto.builder()
        .svcMgmtNum(svcMgmtNum)
        .olmagoCustomerId(olmagoCustomerId)
        .eventDateTime(eventDateTime)
        .build();
  }
  
}
