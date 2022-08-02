package me.realimpact.dummy.swing.domain;

import com.fasterxml.jackson.annotation.OptBoolean;
import me.realimpact.dummy.swing.Util;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServiceOlmagoCustomerRelationHistoryRepository
    extends JpaRepository<ServiceOlmagoCustomerRelationHistory, Long> {

  @Query(
      "SELECT DISTINCT socrh " +
      "FROM   ServiceOlmagoCustomerRelationHistory socrh " +
      "WHERE  (mobilePhoneService = :service OR olmagoCustomer = :olmagoCustomer) " +
      "AND    effEndDtm = :effEndDtm"
  )
  List<ServiceOlmagoCustomerRelationHistory> findRelationHistoryByServiceOrOlmagoCustomer(
      @Param("service") MobilePhoneService mobilePhoneService,
      @Param("olmagoCustomer") OlmagoCustomer olmagoCustomer,
      @Param("effEndDtm") LocalDateTime effEndDtm
  );

  @Query(
      "SELECT DISTINCT socrh " +
      "FROM   ServiceOlmagoCustomerRelationHistory socrh " +
      "WHERE  mobilePhoneService = :service " +
      "AND    olmagoCustomer = :olmagoCustomer " +
      "AND    effEndDtm = :effEndDtm"
  )
  Optional<ServiceOlmagoCustomerRelationHistory> findRelationHistoryByServiceAndOlmagoCustomer(
      @Param("service") MobilePhoneService mobilePhoneService,
      @Param("olmagoCustomer") OlmagoCustomer olmagoCustomer,
      @Param("effEndDtm") LocalDateTime effEndDtm
  );
}
