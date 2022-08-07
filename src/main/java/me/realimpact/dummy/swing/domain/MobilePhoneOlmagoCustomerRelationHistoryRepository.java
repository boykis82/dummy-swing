package me.realimpact.dummy.swing.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MobilePhoneOlmagoCustomerRelationHistoryRepository
    extends JpaRepository<MobilePhoneOlmagoCustomerRelationHistory, Long> {
  @Query(
      "SELECT DISTINCT socrh " +
      "FROM   MobilePhoneOlmagoCustomerRelationHistory socrh " +
      "WHERE  (mobilePhone = :mobilePhone OR olmagoCustomer = :olmagoCustomer) " +
      "AND    effEndDtm = :effEndDtm"
  )
  List<MobilePhoneOlmagoCustomerRelationHistory> findRelationHistoryByMobilePhoneOrOlmagoCustomer(
      @Param("mobilePhone") MobilePhone mobilePhone,
      @Param("olmagoCustomer") OlmagoCustomer olmagoCustomer,
      @Param("effEndDtm") LocalDateTime effEndDtm
  );

  @Query(
      "SELECT DISTINCT socrh " +
      "FROM   MobilePhoneOlmagoCustomerRelationHistory socrh " +
      "WHERE  mobilePhone = :mobilePhone " +
      "AND    olmagoCustomer = :olmagoCustomer " +
      "AND    effEndDtm = :effEndDtm"
  )
  Optional<MobilePhoneOlmagoCustomerRelationHistory> findRelationHistoryByMobilePhoneAndOlmagoCustomer(
      @Param("mobilePhone") MobilePhone mobilePhone,
      @Param("olmagoCustomer") OlmagoCustomer olmagoCustomer,
      @Param("effEndDtm") LocalDateTime effEndDtm
  );
}
