package me.realimpact.dummy.swing.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MobilePhoneRepository extends JpaRepository<MobilePhone, Long> {
  @Query(
      "SELECT DISTINCT s " +
      "FROM MobilePhone s " +
      "JOIN s.customer c " +
      "JOIN s.feeProduct p " +
      "WHERE c.ci = :ci " +
      "AND s.svcTermDt IS NULL " +
      "ORDER BY s.svcMgmtNum DESC"
  )
  List<MobilePhone> findByCI(@Param("ci") String CI);
}
