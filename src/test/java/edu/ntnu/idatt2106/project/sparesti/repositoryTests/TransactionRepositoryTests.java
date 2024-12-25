package edu.ntnu.idatt2106.project.sparesti.repositoryTests;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionEntity;
import edu.ntnu.idatt2106.project.sparesti.repositories.BankAccountRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionCategoryRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Class for testing TransactionRepository operations. This class tests various scenarios involving
 * transaction retrieval based on account, category, and date range. It uses an in-memory database
 * to verify the correctness of the repository's query methods under various conditions.
 */
@DataJpaTest
@ActiveProfiles("test")
public class TransactionRepositoryTests {

  @Autowired private BankAccountRepository bankAccountRepository;

  @Autowired private TransactionRepository transactionRepository;

  @Autowired private TransactionCategoryRepository transactionCategoryRepository;

  @Autowired private TestEntityManager entityManager;

  private BankAccountEntity bankAccount;
  private TransactionCategoryEntity transactionCategory1;
  private TransactionCategoryEntity transactionCategory2;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  /**
   * Sets up the test environment before each test. This includes preparing a bank account,
   * transaction categories, and transactions to be used in the tests.
   */
  @BeforeEach
  public void setup() {
    bankAccount = BankAccountEntity.builder().id(1L).build();
    transactionCategory1 = TransactionCategoryEntity.builder().build();
    transactionCategory2 = TransactionCategoryEntity.builder().build();

    startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
    endDate = LocalDateTime.of(2022, 12, 31, 23, 59);

    entityManager.persist(bankAccount);
    entityManager.persist(transactionCategory1);
    entityManager.persist(transactionCategory2);

    TransactionEntity transaction1 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory1)
            .date(startDate)
            .build();
    TransactionEntity transaction2 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory1)
            .date(startDate.plusDays(5))
            .build();
    TransactionEntity transaction3 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory2)
            .date(endDate)
            .build();
    TransactionEntity transaction4 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory2)
            .date(endDate.plusDays(5))
            .build();

    bankAccount.addTransaction(transaction1);
    bankAccount.addTransaction(transaction2);
    bankAccount.addTransaction(transaction3);
    bankAccount.addTransaction(transaction4);

    entityManager.flush();
  }

  /**
   * Tests retrieval of transactions for a bank account within a specified date range. Expected to
   * find transactions that match both the account ID and the date range.
   */
  @Test
  public void findTransactionsOnAccountBetweenDate() {
    List<TransactionEntity> found =
        transactionRepository.findByBankAccount_IdAndDateBetween(
            bankAccount.getId(), startDate, endDate);
    assertThat(found).hasSize(3);
  }

  /**
   * Tests retrieval of transactions for a bank account within a date range where no transactions
   * occurred. Expected to find no transactions.
   */
  @Test
  public void findNoTransactionsOnAccountBetweenDate() {
    LocalDateTime noTransactionsStart = LocalDateTime.of(2024, 1, 1, 0, 0);
    LocalDateTime noTransactionsEnd = LocalDateTime.of(2024, 12, 31, 23, 59);
    List<TransactionEntity> found =
        transactionRepository.findByBankAccount_IdAndDateBetween(
            bankAccount.getId(), noTransactionsStart, noTransactionsEnd);
    assertThat(found).isEmpty();
  }

  /**
   * Tests retrieval of transactions for a bank account filtered by a specific transaction category.
   * Expected to find transactions that match the account ID and the specified category.
   */
  @Test
  public void findTransactionsOnAccountByCategory() {
    List<TransactionEntity> found =
        transactionRepository.findByBankAccount_IdAndTransactionCategoryEntity_Id(
            bankAccount.getId(), transactionCategory1.getId());
    assertThat(found).hasSize(2);
  }

  /**
   * Tests retrieval of transactions for a bank account with an invalid account ID. Expected to find
   * no transactions as the account ID does not exist.
   */
  @Test
  public void findTransactionsWithInvalidAccountId() {
    Long invalidAccountId = 999L;
    List<TransactionEntity> found =
        transactionRepository.findByBankAccount_IdAndDateBetween(
            invalidAccountId, startDate, endDate);
    assertThat(found).isEmpty();
  }

  /**
   * Tests retrieval of transactions for a bank account filtered by both category and date range.
   * Expected to find transactions that meet all specified criteria.
   */
  @Test
  public void findTransactionsInAccountByCategoryAndDate() {
    List<TransactionEntity> found =
        transactionRepository.findByBankAccount_IdAndTransactionCategoryEntity_IdAndDateBetween(
            bankAccount.getId(), transactionCategory1.getId(), startDate, endDate);
    assertThat(found).hasSize(2);
  }

  /**
   * Tests retrieval of transactions for a bank account with an invalid category ID. Expected to
   * find no transactions as the category ID does not exist.
   */
  @Test
  public void findTransactionsWithInvalidCategoryId() {
    Long invalidCategoryId = 999L;
    List<TransactionEntity> found =
        transactionRepository.findByBankAccount_IdAndTransactionCategoryEntity_Id(
            bankAccount.getId(), invalidCategoryId);
    assertThat(found).isEmpty();
  }
}
