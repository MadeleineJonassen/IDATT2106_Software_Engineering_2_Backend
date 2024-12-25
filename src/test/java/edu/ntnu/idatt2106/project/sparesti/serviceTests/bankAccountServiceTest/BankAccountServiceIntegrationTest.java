package edu.ntnu.idatt2106.project.sparesti.serviceTests.bankAccountServiceTest;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransferDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransferEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.repositories.BankAccountRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionCategoryRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.impl.BankAccountServiceImpl;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/** Integration tests for the BankAccountService class, using the test profile. */
@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class BankAccountServiceIntegrationTest {

  @Autowired private BankAccountServiceImpl bankAccountService;

  @Autowired private BankAccountRepository bankAccountRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private TransactionRepository transactionRepository;

  @Autowired private TransactionCategoryRepository transactionCategoryRepository;

  private BankAccountEntity bankAccountEntity;
  private LocalDateTime date1;
  private LocalDateTime date2;
  private UserEntity user;

  /** Setup before each test. */
  @BeforeEach
  public void setUp() {
    bankAccountEntity = BankAccountEntity.builder().id(1L).sum(1000.0).build();
    date1 = LocalDateTime.of(2022, 1, 1, 0, 0);
    date2 = LocalDateTime.of(2022, 12, 31, 23, 59);
    user = UserEntity.builder().username("John Doe").build();
    userRepository.save(user);
  }

  /**
   * Tests the creation of a bank account using the service and repository layers. Verifies that the
   * account is correctly persisted and retrievable.
   */
  @Test
  public void createBankAccount() {
    BankAccountDto newAccount =
        BankAccountDto.builder().id(5L).userEntityId(user.getId()).sum(200).build();

    BankAccountDto createdAccount = bankAccountService.createBankAccount(newAccount);
    assertNotNull(createdAccount);

    assertTrue(bankAccountRepository.existsById(createdAccount.getId()));
  }

  /**
   * Tests adding a transaction to a bank account. Ensures that the transaction is correctly
   * associated with the bank account and persists in the database.
   */
  @Transactional
  @Test
  public void addTransactionToAccount() {
    bankAccountRepository.save(bankAccountEntity);

    TransactionCategoryEntity transactionCategory = TransactionCategoryEntity.builder().build();
    transactionCategoryRepository.save(transactionCategory);

    TransactionDto transactionDto =
        TransactionDto.builder()
            .sum(500.0)
            .date(date1)
            .categoryEntityId(transactionCategory.getId())
            .build();

    bankAccountService.addTransactionToAccount(1L, transactionDto);
    BankAccountEntity bankAccountFromDB =
        bankAccountRepository.findById(bankAccountEntity.getId()).get();
    TransactionEntity createdTransaction = bankAccountFromDB.getTransactions().get(0);

    assertNotNull(createdTransaction);
    assertTrue(transactionRepository.existsById(createdTransaction.getId()));
    assertTrue(bankAccountFromDB.getTransactions().contains(createdTransaction));
  }

  /**
   * Tests retrieving the total amount spent on transactions of a specific category within a
   * specified date range. Verifies correct calculation of summed amounts.
   */
  @Test
  public void getAmountUsedOnTransactionsOnCategoryBetweenDate() {
    TransactionCategoryEntity transactionCategory1 = TransactionCategoryEntity.builder().build();
    TransactionCategoryEntity transactionCategory2 = TransactionCategoryEntity.builder().build();

    transactionCategoryRepository.save(transactionCategory1);
    transactionCategoryRepository.save(transactionCategory2);

    bankAccountRepository.save(bankAccountEntity);

    TransactionEntity transaction1 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory1)
            .date(date1)
            .sum(-10.0)
            .build();
    TransactionEntity transaction2 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory1)
            .date(date1.plusDays(5))
            .sum(-10.0)
            .build();
    TransactionEntity transaction3 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory2)
            .date(date2)
            .sum(-10.0)
            .build();
    TransactionEntity transaction4 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory2)
            .date(date2.plusDays(5))
            .sum(-10.0)
            .build();

    bankAccountEntity.addTransaction(transaction1);
    bankAccountEntity.addTransaction(transaction2);
    bankAccountEntity.addTransaction(transaction3);
    bankAccountEntity.addTransaction(transaction4);

    bankAccountRepository.save(bankAccountEntity);

    double result =
        bankAccountService.getAmountUsedOnTransactionsOnCategoryBetweenDate(
            bankAccountEntity.getId(), transactionCategory1.getId(), date1, date2);
    assertEquals((transaction1.getSum() + transaction2.getSum()) * -1, result, 0.01);
  }

  /**
   * Tests the functionality of transferring funds between two accounts. Verifies that the transfer
   * is recorded in both the source and destination accounts. Also checks that the sum of the
   * accounts are updated.
   */
  @Transactional
  @Test
  public void addTransferToAccounts() {
    BankAccountEntity bankAccountEntitySource = bankAccountEntity;
    BankAccountEntity bankAccountEntityDestination =
        BankAccountEntity.builder().id(2L).sum(1000.0).build();

    bankAccountRepository.save(bankAccountEntitySource);
    bankAccountRepository.save(bankAccountEntityDestination);

    TransferDto transferDto =
        TransferDto.builder()
            .date(date1)
            .sum(200.0)
            .sourceBankAccountId(bankAccountEntitySource.getId())
            .destinationBankAccountId(bankAccountEntityDestination.getId())
            .build();

    bankAccountService.addTransferToAccounts(transferDto);

    TransferEntity createdTransferFromSourceAccount =
        bankAccountRepository
            .findById(bankAccountEntitySource.getId())
            .get()
            .getTransfersSent()
            .get(0);
    TransferEntity createdTransferFromDestinationAccount =
        bankAccountRepository
            .findById(bankAccountEntityDestination.getId())
            .get()
            .getTransfersReceived()
            .get(0);

    BankAccountEntity sourceBankAccountEntityFromDB =
        bankAccountRepository.findById(bankAccountEntitySource.getId()).get();
    BankAccountEntity destinationBankAccountEntityFromDB =
        bankAccountRepository.findById(bankAccountEntityDestination.getId()).get();

    assertNotNull(createdTransferFromSourceAccount);
    assertNotNull(createdTransferFromDestinationAccount);
    assertEquals(800.0, sourceBankAccountEntityFromDB.getSum(), 0.01);
    assertEquals(1200.0, destinationBankAccountEntityFromDB.getSum(), 0.01);
  }

  /**
   * Tests the method that assigns test accounts to user, and checks if the generated accounts has
   * their intended names.
   */
  @Transactional
  @Test
  public void assignTestBankAccountsToUser() {
    bankAccountService.assignUserDefaultBankAccountSet(user.getId());

    UserEntity userInDb = userRepository.findById(user.getId()).get();
    List<BankAccountEntity> generatedBankAccounts = userInDb.getBankAccounts();
    assertFalse(generatedBankAccounts.isEmpty());
    assertTrue(
        generatedBankAccounts.stream().anyMatch(dto -> dto.getName().equals("Default Sparekonto")));
    assertTrue(
        generatedBankAccounts.stream()
            .anyMatch(dto -> dto.getName().equals("Default Bufferkonto")));
    assertTrue(
        generatedBankAccounts.stream().anyMatch(dto -> dto.getName().equals("Default Brukskonto")));
  }

  /** Tests that find averageExpenditure method successfully projects the expenditure. */
  @Test
  public void findAverageExpenditure() {
    TransactionCategoryEntity transactionCategory1 = TransactionCategoryEntity.builder().build();
    TransactionCategoryEntity transactionCategory2 = TransactionCategoryEntity.builder().build();

    LocalDateTime localDateTime = LocalDateTime.now();
    transactionCategoryRepository.save(transactionCategory1);
    transactionCategoryRepository.save(transactionCategory2);

    bankAccountRepository.save(bankAccountEntity);

    TransactionEntity transaction1 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory1)
            .date(localDateTime.minusDays(3))
            .sum(-10.0)
            .build();
    TransactionEntity transaction2 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory1)
            .date(localDateTime.minusDays(6))
            .sum(-10.0)
            .build();
    TransactionEntity transaction3 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory2)
            .date(localDateTime.minusDays(8))
            .sum(-10.0)
            .build();
    TransactionEntity transaction4 =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory1)
            .date(localDateTime.minusDays(11))
            .sum(10.0)
            .build();

    bankAccountEntity.addTransaction(transaction1);
    bankAccountEntity.addTransaction(transaction2);
    bankAccountEntity.addTransaction(transaction3);
    bankAccountEntity.addTransaction(transaction4);
    bankAccountRepository.save(bankAccountEntity);

    double result =
        bankAccountService.getAverageExpenditureOnCategory(
            bankAccountEntity.getId(), transactionCategory1.getId(), 30);
    assertEquals(20, result);
  }
}
