package me.realimpact.dummy.swing.domain;

import com.fasterxml.jackson.annotation.OptBoolean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceOlmagoCustomerRelationHistoryRepository extends JpaRepository<ServiceOlmagoCustomerRelationHistory, Long> {
  @Query("SELECT DISTINCT socrh FROM ServiceOlmagoCustomerRelationHistory socrh WHERE (service = :service or olmagoCustomer = :olmagoCustomer) AND effEndDtm = LocalDateTime.MAX")
  List<ServiceOlmagoCustomerRelationHistory> findActiveHistoryByServiceOrOlmagoCustomer(MobilePhoneService mobilePhoneService, OlmagoCustomer olmagoCustomer);
  
  @Query("SELECT DISTINCT socrh FROM ServiceOlmagoCustomerRelationHistory socrh WHERE service = :service AND olmagoCustomer = :olmagoCustomer AND effEndDtm = LocalDateTime.MAX")
  Optional<ServiceOlmagoCustomerRelationHistory> findActiveHistoryByServiceAndOlmagoCustomer(MobilePhoneService mobilePhoneService, OlmagoCustomer olmagoCustomer);
}
