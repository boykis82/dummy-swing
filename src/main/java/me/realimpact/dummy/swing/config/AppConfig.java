package me.realimpact.dummy.swing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.realimpact.dummy.swing.proxy.OlmagoProxy;
import me.realimpact.dummy.swing.proxy.OlmagoProxyImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AppConfig {
  @Bean
  public AuditorAware<String> auditorAware() {
    return () -> Optional.of("OLMAGO");
  }
  
  @Bean
  public ObjectMapper mapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }
  
  @Bean
  public WebClient olmagoWebClient() {
    return WebClient.create("http://olmago/api/v1");
  }
  
  @Bean
  public OlmagoProxy olmagoProxy() {
    return new OlmagoProxyImpl(olmagoWebClient());
  }
}
