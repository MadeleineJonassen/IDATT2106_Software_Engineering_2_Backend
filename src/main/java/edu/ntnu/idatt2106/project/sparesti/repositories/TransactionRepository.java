package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for TransactionEntity. This interface extends JpaRepository to handle data
 * access operations for TransactionEntity objects, providing a standard set of CRUD operations and
 * the ability to query transactions based on various criteria.
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
  /**
   * Finds all transactions for a given bank account within a specified date range.
   *
   * @param bankAccountId The ID of the bank account.
   * @param startDate The start date of the date range.
   * @param endDate The end date of the date range.
   * @return A list of TransactionEntity objects that fall within the specified date range for the
   *     specified bank account.
   */
  List<TransactionEntity> findByBankAccount_IdAndDateBetween(
      Long bankAccountId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Finds all transactions for a given bank account that are categorized under a specific
   * transaction category.
   *
   * @param bankAccountId The ID of the bank account.
   * @param transactionCategoryId The ID of the transaction category.
   * @return A list of TransactionEntity objects that match the specified bank account and
   *     transaction category.
   */
  List<TransactionEntity> findByBankAccount_IdAndTransactionCategoryEntity_Id(
      Long bankAccountId, Long transactionCategoryId);

  /**
   * Finds all transactions for a given bank account, under a specific transaction category, within
   * a specified date range.
   *
   * @param bankAccountId The ID of the bank account.
   * @param transactionCategoryId The ID of the transaction category.
   * @param startDate The start date of the date range.
   * @param endDate The end date of the date range.
   * @return A list of TransactionEntity objects that meet the specified criteria.
   */
  List<TransactionEntity> findByBankAccount_IdAndTransactionCategoryEntity_IdAndDateBetween(
      Long bankAccountId,
      Long transactionCategoryId,
      LocalDateTime startDate,
      LocalDateTime endDate);
}
