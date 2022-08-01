package me.realimpact.dummy.swing.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MobilePhoneServiceRepository extends JpaRepository<MobilePhoneService, Long> {
  @Query("SELECT DISTINCT s FROM MobilePhoneService s join s.customer c WHERE c.ci = :ci AND s.svcTermDt is null ORDER BY s.svcMgmtNum DESC")
  List<MobilePhoneService> findByCI(@Param("ci") String CI);
}
