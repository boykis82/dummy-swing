package me.realimpact.dummy.swing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.realimpact.dummy.swing.dto.ReqRelMobilePhoneAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.MobilePhoneAndOlmagoRelationResponseDto;
import me.realimpact.dummy.swing.exception.BusinessException;
import me.realimpact.dummy.swing.service.OlmagoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static me.realimpact.dummy.swing.exception.BusinessExceptionReason.*;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OlmagoController.class)
public class OlmagoControllerTest {
  @Autowired
  private MockMvc mvc;
  
  @MockBean
  private OlmagoService olmagoService;

  @Autowired
  ObjectMapper mapper;
  
  private static final String MOBILE_PHONE_BASE_URL = "/swing/api/v1/mobile-phones";
  private static final String MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_BASE_URL = MOBILE_PHONE_BASE_URL + "/{svc-mgmt-num}/linked-olmago-customer";
  private static final String MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_URL = MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_BASE_URL + "/{olmago-customer-id}";

  @Test
  public void givenNotExistedService_whenLinkServiceAndOlmago_thenShouldReturnBadRequest() throws Exception {
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L, LocalDateTime.now());
    given(olmagoService.linkOlmagoCustomerWithMobilePhoneService(dto))
        .willThrow(new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF));
  
    mvc.perform(
        post(MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_BASE_URL, 1)
            .content(mapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
  
  @Test
  public void givenMismatchCustomer_whenLinkServiceAndOlmago_thenShouldReturnPreconditionFailed() throws Exception {
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L, LocalDateTime.now());
    given(olmagoService.linkOlmagoCustomerWithMobilePhoneService(dto))
        .willThrow(new BusinessException(CUSTOMER_MISMATCH));
    
    mvc.perform(
            post(MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_BASE_URL, 1)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isPreconditionFailed())
        .andDo(print());
  }
  
  @Test
  public void givenExistedRelation_whenLinkServiceAndOlmago_thenShouldReturnConflict() throws Exception {
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L, LocalDateTime.now());
    given(olmagoService.linkOlmagoCustomerWithMobilePhoneService(dto))
        .willThrow(new BusinessException(SERVICE_OLMAGO_RELATION_EXISTED));
    
    mvc.perform(
            post(MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_BASE_URL, 1)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andDo(print());
  }
  
  @Test
  public void givenNotExistedRelation_whenLinkServiceAndOlmago_thenShouldBeOk() throws Exception {
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L, LocalDateTime.now());
    given(olmagoService.linkOlmagoCustomerWithMobilePhoneService(dto))
        .willReturn(
            MobilePhoneAndOlmagoRelationResponseDto.builder()
                .svcMgmtNum(1L)
                .olmagoCustomerId(2L)
                .eventDataTime(LocalDateTime.now())
                .build()
        );
  
    mvc.perform(
            post(MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_BASE_URL, 1)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }
  
  @Test
  public void givenNotExistedService_whenUnlinkServiceAndOlmago_thenShouldReturnBadRequest() throws Exception {
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L, LocalDateTime.now());
    given(olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto))
        .willThrow(new BusinessException(SERVICE_NOT_FOUND_BY_EXT_REF));
    
    mvc.perform(
            put(MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_URL, 1, 2)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
  
  @Test
  public void givenNotExistedOlmagoCustomer_whenUnlinkServiceAndOlmago_thenShouldReturnBadRequest() throws Exception {
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L, LocalDateTime.now());
    given(olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto))
        .willThrow(new BusinessException(CUSTOMER_NOT_FOUND_BY_EXT_REF));
    
    mvc.perform(
            put(MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_URL, 1, 2)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
  
  @Test
  public void givenNotExistedRelation_whenUnlinkServiceAndOlmago_thenShouldReturnNotFound() throws Exception {
    ReqRelMobilePhoneAndOlmagoCustDto dto = buildReqRelSvcAndOlmagoCustDto(1L, 2L, LocalDateTime.now());
    given(olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(dto))
        .willThrow(new BusinessException(SERVICE_OLMAGO_RELATION_NOT_EXISTED));
    
    mvc.perform(
            put(MOBILE_PHONE_UNDER_OLMAGO_CUSTOMER_URL, 1, 2)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }
  
  private ReqRelMobilePhoneAndOlmagoCustDto buildReqRelSvcAndOlmagoCustDto(long svcMgmtNum, long olmagoCustomerId, LocalDateTime eventDateTime) {
    return ReqRelMobilePhoneAndOlmagoCustDto.builder()
        .svcMgmtNum(svcMgmtNum)
        .olmagoCustomerId(olmagoCustomerId)
        .eventDateTime(eventDateTime)
        .build();
  }
}
