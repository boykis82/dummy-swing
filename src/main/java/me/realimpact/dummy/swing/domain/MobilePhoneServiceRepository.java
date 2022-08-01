package me.realimpact.dummy.swing.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MobilePhoneServiceRepository extends JpaRepository<MobilePhoneService, Long> {
  @Query("SELECT DISTINCT s FROM MobilePhoneService s join Customer c WHERE c.ci = :ci AND s.svcTermDt is null ORDER BY s.svcMgmtNum DESC")
  List<MobilePhoneService> findByCI(String CI);
}
