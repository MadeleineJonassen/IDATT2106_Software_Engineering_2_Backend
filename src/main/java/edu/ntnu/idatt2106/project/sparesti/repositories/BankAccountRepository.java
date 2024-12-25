package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for BankAccountEntity handling data access operations. Extends JpaRepository
 * to provide standard CRUD operations and pagination capabilities. This interface includes
 * additional custom queries specifically for BankAccountEntity.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {}
