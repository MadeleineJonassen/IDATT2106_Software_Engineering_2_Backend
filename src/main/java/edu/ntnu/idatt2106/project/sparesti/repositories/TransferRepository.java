package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for TransferEntity. This interface extends JpaRepository to handle data
 * access operations for TransferEntity objects.
 */
@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long> {}
