package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for TransactionCategory entities. This interface is used for data access
 * operations on TransactionCategory entities, providing standard CRUD operations via JpaRepository.
 * It allows for handling of transaction categories within the database, offering capabilities such
 * as finding, saving, and deleting transaction categories.
 */
@Repository
public interface TransactionCategoryRepository
    extends JpaRepository<TransactionCategoryEntity, Long> {
  List<TransactionCategoryEntity> findByIdNotIn(List<Long> ids);
}
