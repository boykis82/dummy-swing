package me.realimpact.dummy.swing.web;

import me.realimpact.dummy.swing.Fixtures;
import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import me.realimpact.dummy.swing.service.MobilePhoneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MobilePhoneController.class)
public class MobilePhoneControllerTest {
  @Autowired
  private MockMvc mvc;
  
  @MockBean
  private MobilePhoneService mobilePhoneService;

  private static final String MOBILE_PHONE_BASE_URL = "/swing/api/v1/mobile-phones";

  @Test
  public void givenExistedServicesAndCi_whenGetServicesByCI_thenShouldReturnServices() throws Exception {
    List<MobilePhoneResponseDto> mobilePhoneResponseDtos = Fixtures.createManyMobilePhoneResponseDtos();
    String testCi = "22222";
    
    given(mobilePhoneService.getMobilePhonesByCi(testCi))
        .willReturn(mobilePhoneResponseDtos);
  
    mvc.perform(get(MOBILE_PHONE_BASE_URL)
            .param("ci", testCi)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(mobilePhoneResponseDtos.size())))
        .andExpect(jsonPath("$[0].svcMgmtNum", is(1)));
  }
  
  @Test
  public void givenNotExistedServicesAndCi_whenGetServicesByCI_thenShouldReturnEmpty() throws Exception {
    String testCi = "22222";
    
    given(mobilePhoneService.getMobilePhonesByCi(testCi))
        .willReturn(Collections.emptyList());
    
    mvc.perform(
        get(MOBILE_PHONE_BASE_URL)
            .param("ci", testCi)
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)))
        .andDo(print());
  }
}
