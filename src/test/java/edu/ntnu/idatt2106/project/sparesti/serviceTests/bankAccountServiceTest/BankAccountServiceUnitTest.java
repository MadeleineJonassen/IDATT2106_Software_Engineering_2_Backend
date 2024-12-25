package edu.ntnu.idatt2106.project.sparesti.serviceTests.bankAccountServiceTest;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransferDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransferEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.BankAccountRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionCategoryRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransferRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.TestIdCounterService;
import edu.ntnu.idatt2106.project.sparesti.services.impl.BankAccountServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Unit tests for the BankAccountServiceImpl class. */
@SpringBootTest
@ActiveProfiles("test")
public class BankAccountServiceUnitTest {
  @Mock private BankAccountRepository bankAccountRepository;
  @Mock private UserRepository userRepository;
  @Mock private TransactionRepository transactionRepository;
  @Mock private TransactionCategoryRepository transactionCategoryRepository;
  @Mock private TransferRepository transferRepository;
  @Mock private TestIdCounterService testIdCounterService;
  @InjectMocks private BankAccountServiceImpl bankAccountService;
  @Mock private Mapper<BankAccountEntity, BankAccountDto> bankAccountMapper;
  @Mock private Mapper<TransactionEntity, TransactionDto> transactionMapper;
  @Mock private Mapper<TransferEntity, TransferDto> transferMapper;

  /** Sets up mocks and injects mock dependencies before each test. */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void shouldThrowWhenCreatingExistingBankAccount() {
    BankAccountDto bankAccountDto = new BankAccountDto();
    bankAccountDto.setId(1L);

    when(bankAccountRepository.existsById(anyLong())).thenReturn(true);

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.BAD_REQUEST, "That account is already registered.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> bankAccountService.createBankAccount(bankAccountDto));

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  @Test
  public void shouldThrowWhenCreatingBankAccountWithNonExistentUser() {
    BankAccountDto bankAccountDto = BankAccountDto.builder().id(1L).userEntityId(1L).build();

    when(bankAccountRepository.existsById(anyLong())).thenReturn(false);
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Bank Account User.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> bankAccountService.createBankAccount(bankAccountDto));

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /**
   * Tests successful creation of a bank account, verifying correct interactions and data handling.
   */
  @Test
  public void testCreateBankAccountSuccess() {
    BankAccountDto bankAccountDto = BankAccountDto.builder().id(1L).userEntityId(1L).build();

    UserEntity userEntity = UserEntity.builder().build();
    BankAccountEntity bankAccountEntity = BankAccountEntity.builder().build();

    when(bankAccountRepository.existsById(anyLong())).thenReturn(false);
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
    when(bankAccountMapper.mapFrom(any(BankAccountDto.class))).thenReturn(bankAccountEntity);
    when(bankAccountMapper.mapTo(any(BankAccountEntity.class))).thenReturn(bankAccountDto);

    System.out.println(userEntity);
    BankAccountDto result = bankAccountService.createBankAccount(bankAccountDto);

    assertNotNull(result);
    verify(userRepository).save(any(UserEntity.class));
  }

  @Test
  public void shouldThrowWhenFetchingTransactionAmountsWithMissingBankAccount() {
    when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Bank Account.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () ->
                bankAccountService.getAmountUsedOnTransactionsOnCategoryBetweenDate(
                    1L, 1L, LocalDateTime.now().minusDays(1), LocalDateTime.now()));

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /**
   * Tests that an exception is thrown when fetching transaction amounts if the transaction category
   * does not exist.
   */
  @Test
  public void shouldThrowWhenFetchingTransactionAmountsWithMissingCategory() {
    when(bankAccountRepository.findById(anyLong()))
        .thenReturn(Optional.of(mock(BankAccountEntity.class)));
    when(transactionCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Transaction category.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () ->
                bankAccountService.getAmountUsedOnTransactionsOnCategoryBetweenDate(
                    1L, 1L, LocalDateTime.now().minusDays(1), LocalDateTime.now()));

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /**
   * Tests successful fetching of transaction amounts within a specified date range for a given
   * category.
   */
  @Test
  public void testGetAmountUsedOnTransactionsOnCategoryBetweenDateSuccess() {
    LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    LocalDateTime endDate = LocalDateTime.now();

    TransactionEntity transaction1 = TransactionEntity.builder().sum(-100.0).build();
    TransactionEntity transaction2 = TransactionEntity.builder().sum(-150.0).build();
    List<TransactionEntity> transactions = Arrays.asList(transaction1, transaction2);

    when(bankAccountRepository.findById(anyLong()))
        .thenReturn(Optional.of(mock(BankAccountEntity.class)));
    when(transactionCategoryRepository.findById(anyLong()))
        .thenReturn(Optional.of(mock(TransactionCategoryEntity.class)));
    when(transactionRepository.findByBankAccount_IdAndTransactionCategoryEntity_IdAndDateBetween(
            anyLong(), anyLong(), any(), any()))
        .thenReturn(transactions);

    double result =
        bankAccountService.getAmountUsedOnTransactionsOnCategoryBetweenDate(
            1L, 1L, startDate, endDate);

    assertEquals(250.0, result, 0.01);
  }

  /** Tests that an exception is thrown when adding a transaction with an invalid category ID. */
  @Test
  public void shouldThrowWhenAddingTransactionWithInvalidCategory() {
    TransactionDto transactionDto = TransactionDto.builder().categoryEntityId(1L).build();

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Transaction category.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              bankAccountService.addTransactionToAccount(1L, transactionDto);
            });

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /** Tests that an exception is thrown when attempting to add a transaction with a null date. */
  @Test
  public void shouldThrowWhenAddingTransactionWithNullDate() {
    when(transactionCategoryRepository.findById(anyLong()))
        .thenReturn(Optional.of(mock(TransactionCategoryEntity.class)));
    TransactionDto transactionDto = TransactionDto.builder().categoryEntityId(1L).build();

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date cannot be null.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              bankAccountService.addTransactionToAccount(1L, transactionDto);
            });

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /** Tests that an exception is thrown when adding a transaction to a non-existent bank account. */
  @Test
  public void shouldThrowWhenAddingTransactionToNonExistentBankAccount() {
    when(transactionCategoryRepository.findById(anyLong()))
        .thenReturn(Optional.of(mock(TransactionCategoryEntity.class)));
    TransactionDto transactionDto =
        TransactionDto.builder().categoryEntityId(1L).date(LocalDateTime.now()).build();

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Bank Account.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              bankAccountService.addTransactionToAccount(1L, transactionDto);
            });

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /** Tests that an exception is thrown when adding a transaction with non-null ID. */
  @Test
  public void shouldThrowWhenAddingTransferWithNonNullId() {
    TransferDto transferDto =
        TransferDto.builder().id(1L).sourceBankAccountId(1L).destinationBankAccountId(2L).build();

    ResponseStatusException expectedException =
        new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Transfer id is auto-generated and cannot be predeterminedly set.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              bankAccountService.addTransferToAccounts(transferDto);
            });

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /**
   * Tests successful addition of a transaction to an account, ensuring the correct update of bank
   * account details.
   */
  @Test
  public void testAddTransactionToAccountSuccess() {
    TransactionDto transactionDto =
        TransactionDto.builder().date(LocalDateTime.now()).categoryEntityId(1L).build();

    BankAccountEntity bankAccountEntity = BankAccountEntity.builder().id(1L).build();
    TransactionEntity transactionEntity = TransactionEntity.builder().build();

    when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankAccountEntity));
    when(transactionMapper.mapFrom(any(TransactionDto.class))).thenReturn(transactionEntity);
    when(transactionCategoryRepository.findById(anyLong()))
        .thenReturn(Optional.of(mock(TransactionCategoryEntity.class)));

    bankAccountService.addTransactionToAccount(1L, transactionDto);

    verify(bankAccountRepository).save(bankAccountEntity);
    assertTrue(bankAccountEntity.getTransactions().contains(transactionEntity));
  }

  /** Tests that an exception is thrown when adding a transfer with a null date. */
  @Test
  public void shouldThrowWhenAddingTransferWithNullDate() {
    TransferDto transferDto =
        TransferDto.builder().sourceBankAccountId(1L).destinationBankAccountId(2L).build();

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date cannot be null.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              bankAccountService.addTransferToAccounts(transferDto);
            });

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /** Tests that an exception is thrown when adding a transfer with non-null ID. */
  @Test
  public void shouldThrowWhenAddingTransactionWithNonNullId() {
    when(transactionCategoryRepository.findById(anyLong()))
        .thenReturn(Optional.of(mock(TransactionCategoryEntity.class)));
    TransactionDto transactionDto = TransactionDto.builder().id(1L).categoryEntityId(1L).build();

    ResponseStatusException expectedException =
        new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Transaction id is auto-generated and cannot be predeterminedly set.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              bankAccountService.addTransactionToAccount(1L, transactionDto);
            });

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /** Tests that an exception is thrown when adding a transfer with a non-null ID. */
  @Test
  public void shouldThrowWhenSearchingForNonExistentBankAccount() {
    when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Bank Account.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              bankAccountService.findOne(1L);
            });

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /** Tests successful addition of a transfer between two accounts. */
  @Test
  public void testAddTransferToAccountsSuccess() {
    TransferDto transferDto =
        TransferDto.builder()
            .date(LocalDateTime.now())
            .sum(100.0)
            .sourceBankAccountId(1L)
            .destinationBankAccountId(2L)
            .build();

    BankAccountEntity sourceAccount = BankAccountEntity.builder().id(1L).sum(1000.0).build();
    BankAccountEntity destinationAccount = BankAccountEntity.builder().id(2L).sum(500.0).build();
    TransferEntity transferEntity =
        TransferEntity.builder()
            .sourceBankAccount(sourceAccount)
            .destinationBankAccount(destinationAccount)
            .build();

    when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
    when(bankAccountRepository.findById(2L)).thenReturn(Optional.of(destinationAccount));
    when(transferMapper.mapFrom(any(TransferDto.class))).thenReturn(transferEntity);

    when(transferRepository.save(transferEntity)).thenReturn(transferEntity);

    bankAccountService.addTransferToAccounts(transferDto);

    assertTrue(sourceAccount.getTransfersSent().contains(transferEntity));
    assertTrue(destinationAccount.getTransfersReceived().contains(transferEntity));

    assertSame(sourceAccount, transferEntity.getSourceBankAccount());
    assertSame(destinationAccount, transferEntity.getDestinationBankAccount());
  }

  /** Tests that adding a transfer between accounts successfully changes the sum of the accounts. */
  @Test
  public void testAddTransferToAccountsUpdatesBalances() {
    TransferDto transferDto =
        TransferDto.builder()
            .date(LocalDateTime.now())
            .sum(100)
            .sourceBankAccountId(1L)
            .destinationBankAccountId(2L)
            .build();

    BankAccountEntity sourceAccount = BankAccountEntity.builder().id(1L).sum(1000).build();
    BankAccountEntity destinationAccount = BankAccountEntity.builder().id(2L).sum(500).build();

    TransferEntity transferEntity =
        TransferEntity.builder()
            .sourceBankAccount(sourceAccount)
            .destinationBankAccount(destinationAccount)
            .sum(100)
            .build();

    when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
    when(bankAccountRepository.findById(2L)).thenReturn(Optional.of(destinationAccount));
    when(transferMapper.mapFrom(any(TransferDto.class))).thenReturn(transferEntity);

    bankAccountService.addTransferToAccounts(transferDto);

    assertEquals(900, sourceAccount.getSum());
    assertEquals(600, destinationAccount.getSum());
  }

  /** Tests that an exception is thrown when searching for a non-existent bank account. */
  @Test
  public void testFindOneThrowsBankAccountNotFoundException() {
    when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Bank Account.");
    Exception exception =
        assertThrows(ResponseStatusException.class, () -> bankAccountService.findOne(1L));

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /** Test for successful assignment of default bank accounts to a user. */
  @Test
  public void testAssignUserDefaultBankAccountSetSuccess() {
    Long userId = 1L;
    UserEntity userEntity = UserEntity.builder().id(userId).build();

    BankAccountDto bankAccountDto = BankAccountDto.builder().id(1L).userEntityId(userId).build();

    BankAccountEntity defaultSavingsAccount =
        BankAccountEntity.builder().id(1L).name("Default Sparekonto").build();
    BankAccountEntity defaultBufferAccount =
        BankAccountEntity.builder().id(1L).name("Default Bufferkonto").build();
    BankAccountEntity defaultCheckingAccount =
        BankAccountEntity.builder().id(1L).name("Default Brukskonto").build();

    when(bankAccountRepository.existsById(anyLong())).thenReturn(false);
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
    when(bankAccountMapper.mapFrom(any(BankAccountDto.class)))
        .thenReturn(defaultSavingsAccount, defaultBufferAccount, defaultCheckingAccount);
    when(bankAccountMapper.mapTo(any(BankAccountEntity.class))).thenReturn(bankAccountDto);
    when(testIdCounterService.getNextUniqueId()).thenReturn(1L, 2L, 3L);

    bankAccountService.assignUserDefaultBankAccountSet(userId);
    List<BankAccountEntity> generatedBankAccounts = userEntity.getBankAccounts();
    assertEquals(3, generatedBankAccounts.size());
    assertTrue(
        generatedBankAccounts.stream().anyMatch(dto -> dto.getName().equals("Default Sparekonto")));
    assertTrue(
        generatedBankAccounts.stream()
            .anyMatch(dto -> dto.getName().equals("Default Bufferkonto")));
    assertTrue(
        generatedBankAccounts.stream().anyMatch(dto -> dto.getName().equals("Default Brukskonto")));
  }

  /** Test that an exception is thrown if the user does not exist. */
  @Test
  public void shouldThrowWhenAssigningDefaultBankAccountsToNonExistentUser() {
    Long userId = 1L;

    when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

    ResponseStatusException expectedException =
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Bank Account User.");
    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> bankAccountService.assignUserDefaultBankAccountSet(userId));

    assertEquals(expectedException.getMessage(), exception.getMessage());
  }

  /** Tests the findAverageExpenditure method. */
  @Test
  public void testFindAverageExpenditure() {
    TransactionEntity transaction1 = TransactionEntity.builder().sum(-100).build();
    TransactionEntity transaction2 = TransactionEntity.builder().sum(-300).build();
    TransactionEntity transaction3 = TransactionEntity.builder().sum(-500).build();
    List<TransactionEntity> transactions = new ArrayList<>();
    transactions.add(transaction1);
    transactions.add(transaction2);
    transactions.add(transaction3);

    BankAccountEntity bankAccountEntity = BankAccountEntity.builder().build();

    when(transactionCategoryRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(TransactionCategoryEntity.builder().build()));
    when(bankAccountRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(bankAccountEntity));
    when(transactionRepository.findByBankAccount_IdAndTransactionCategoryEntity_IdAndDateBetween(
            any(), any(), any(), any()))
        .thenReturn(transactions);

    double average = bankAccountService.getAverageExpenditureOnCategory(1L, 1L, 7);
    assertEquals(210, average);
  }
}
