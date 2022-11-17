package me.realimpact.dummy.swing.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.realimpact.dummy.swing.domain.Product.ProductTier;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class OlmagoProxyTest {
  private static MockWebServer mockBackEnd;
  
  private final ObjectMapper mapper = new ObjectMapper();
  private final String baseUrl = String.format("http://localhost:%s/olmago/api/v1", mockBackEnd.getPort());
  
  @BeforeClass
  public static void beforeClass() throws IOException  {
    mockBackEnd = new MockWebServer();
    mockBackEnd.start();
  }
  
  @AfterClass
  public static void afterClass() throws IOException {
    mockBackEnd.shutdown();
  }

  @Test
  public void whenUnlinkMobilePhoneService_thenShouldBeOk() throws Exception {
    mockBackEnd.enqueue(new MockResponse()
        .addHeader("Content-Type", "application/json")
    );
    
    MobilePhoneDto dto = MobilePhoneDto.builder().svcMgmtNum(7102112312L).build();
  
    StepVerifier.create(
        (new OlmagoProxyImpl(WebClient.create(baseUrl)))
            .unlinkMobilePhoneService(1L, dto)
    ).verifyComplete();
  
    RecordedRequest recordedRequest = mockBackEnd.takeRequest();
    assertThat(recordedRequest.getMethod()).isEqualTo("PUT");
    assertThat(recordedRequest.getPath()).isEqualTo("/olmago/api/v1/customers/1/unlinkMobilePhone");
  }
  
  @Test
  public void whenApplyMobilePhoneLinkedDiscount_thenShouldBeOk() throws Exception {
    MobilePhoneDto dto = MobilePhoneDto.builder().svcMgmtNum(7102112312L).mobilePhonePricePlan("PLATINUM").build();

    mockBackEnd.enqueue(
        new MockResponse()
            .setBody(mapper.writeValueAsString(dto))
            .addHeader("Content-Type", "application/json")
    );
    
    StepVerifier.create(
        (new OlmagoProxyImpl(WebClient.create(baseUrl)))
            .applyMobilePhoneLinkedDiscount(1L, dto)
    ).verifyComplete();
    
    RecordedRequest recordedRequest = mockBackEnd.takeRequest();
    assertThat(recordedRequest.getMethod()).isEqualTo("PUT");
    assertThat(recordedRequest.getPath()).isEqualTo("/olmago/api/v1/customers/1/changeMobilePhonePricePlan");
  }
}
