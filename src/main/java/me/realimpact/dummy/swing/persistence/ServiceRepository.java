package me.realimpact.dummy.swing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
  @Query("SELECT DISTINCT s FROM Service s join Customer c WHERE c.ci = :ci AND s.svcTermDt is null")
  List<Service> findByCI(String CI);
}
