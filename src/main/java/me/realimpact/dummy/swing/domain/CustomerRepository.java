package me.realimpact.dummy.swing.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
  @Query("SELECT DISTINCT c FROM Customer c WHERE c.ci = :ci")
  Optional<Customer> findByCI(@Param("ci") String CI);
}
