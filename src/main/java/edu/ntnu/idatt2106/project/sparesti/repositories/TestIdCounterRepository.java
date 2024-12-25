package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.TestIdCounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for TestIdCounter entities. */
@Repository
public interface TestIdCounterRepository extends JpaRepository<TestIdCounterEntity, Long> {}
