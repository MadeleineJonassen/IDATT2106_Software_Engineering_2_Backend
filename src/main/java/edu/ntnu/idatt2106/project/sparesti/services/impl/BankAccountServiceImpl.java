package edu.ntnu.idatt2106.project.sparesti.services.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.ProjectedDailyExpenseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransferDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransferEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.BankAccountRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionCategoryRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransferRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.BankAccountService;
import edu.ntnu.idatt2106.project.sparesti.services.TestIdCounterService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of BankAccountService to manage bank account related operations. This class
 * provides detailed implementations for creating, querying, and updating bank account data,
 * handling transactions, and managing transfers between accounts.
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

  private BankAccountRepository bankAccountRepository;
  private TransactionRepository transactionRepository;
  private TransactionCategoryRepository transactionCategoryRepository;
  private TransferRepository transferRepository;
  private UserRepository userRepository;
  private TestIdCounterService testIdCounterService;
  private Mapper<BankAccountEntity, BankAccountDto> bankAccountMapper;
  private Mapper<TransactionEntity, TransactionDto> transactionMapper;
  private Mapper<TransferEntity, TransferDto> transferMapper;

  private static final String DEFAULT_SAVING_ACCOUNT = "Default Sparekonto";
  private static final String DEFAULT_BUFFER_ACCOUNT = "Default Bufferkonto";
  private static final String DEFAULT_CHECKING_ACCOUNT = "Default Brukskonto";
  private static final long BASE_ACCOUNT_ID = 1000000L;
  private static final int BASE_SUM = 50000;
  private static final int SUM_VARIATION_LIMIT = 100;
  private static final int SUM_VARIATION_MULTIPLIER = 1000;

  /**
   * Constructs a BankAccountServiceImpl with necessary dependencies. Used for field injection.
   *
   * @param bankAccountRepository repository for bank account operations
   * @param transactionRepository repository for transaction operations
   * @param transactionCategoryRepository repository for transaction category operations
   * @param transferRepository repository for transfer operations
   * @param testIdCounterService service for finding the next id.
   * @param userRepository repository for user operations
   * @param bankAccountMapper mapper for converting between BankAccountEntity and BankAccountDto
   * @param transactionMapper mapper for converting between TransactionEntity and TransactionDto
   * @param transferMapper mapper for converting between TransferEntity and TransferDto
   */
  public BankAccountServiceImpl(
      BankAccountRepository bankAccountRepository,
      TransactionRepository transactionRepository,
      TransactionCategoryRepository transactionCategoryRepository,
      TransferRepository transferRepository,
      UserRepository userRepository,
      TestIdCounterService testIdCounterService,
      Mapper<BankAccountEntity, BankAccountDto> bankAccountMapper,
      Mapper<TransactionEntity, TransactionDto> transactionMapper,
      Mapper<TransferEntity, TransferDto> transferMapper) {
    this.bankAccountRepository = bankAccountRepository;
    this.transactionRepository = transactionRepository;
    this.transactionCategoryRepository = transactionCategoryRepository;
    this.testIdCounterService = testIdCounterService;
    this.transferRepository = transferRepository;
    this.userRepository = userRepository;
    this.bankAccountMapper = bankAccountMapper;
    this.transactionMapper = transactionMapper;
    this.transferMapper = transferMapper;
  }

  /**
   * {@inheritDoc} Throws BankAccountAlreadyRegisteredException if the bank account ID already
   * exists. Throws UserNotFoundException if the associated user is not found.
   */
  @Override
  public BankAccountDto createBankAccount(BankAccountDto bankAccountDto) {
    validateBankAccountDto(bankAccountDto);

    UserEntity user =
        userRepository
            .findById(bankAccountDto.getUserEntityId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Could not find Bank Account User."));

    BankAccountEntity bankAccountEntity = bankAccountMapper.mapFrom(bankAccountDto);

    user.addBankAccount(bankAccountEntity);

    userRepository.save(user);

    return bankAccountMapper.mapTo(bankAccountEntity);
  }

  /**
   * {@inheritDoc} Throws BankAccountNotFoundException if the bank account is not found. Throws
   * CategoryNotFoundException if the transaction category is not found.
   */
  @Override
  public double getAmountUsedOnTransactionsOnCategoryBetweenDate(
      Long bankAccountId, Long categoryId, LocalDateTime startDate, LocalDateTime endDate) {
    bankAccountRepository
        .findById(bankAccountId)
        .orElseThrow(
            () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Bank Account."));

    checkForCategoryNotFoundException(categoryId);

    List<TransactionEntity> transactionsFound =
        transactionRepository.findByBankAccount_IdAndTransactionCategoryEntity_IdAndDateBetween(
            bankAccountId, categoryId, startDate, endDate);

    return Math.abs(
        transactionsFound.stream()
            .filter(transaction -> transaction.getSum() < 0)
            .mapToDouble(TransactionEntity::getSum)
            .sum());
  }

  /** {@inheritDoc} Throws BankAccountNotFoundException if the bank account is not found. */
  @Override
  public BankAccountDto findOne(Long accountId) {
    return bankAccountMapper.mapTo(
        bankAccountRepository
            .findById(accountId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Could not find Bank Account.")));
  }

  /**
   * {@inheritDoc} Throws CategoryNotFoundException if the transaction category is not found. Throws
   */
  @Transactional
  @Override
  public void addTransactionToAccount(Long accountId, TransactionDto transactionDto) {
    validateTransactionDto(transactionDto);

    TransactionEntity transactionEntity = transactionMapper.mapFrom(transactionDto);

    BankAccountEntity bankAccountEntity =
        bankAccountRepository
            .findById(accountId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Could not find Bank Account."));

    bankAccountEntity.addTransaction(transactionEntity);

    bankAccountRepository.save(bankAccountEntity);

    updateBalanceOfAccount(bankAccountEntity, transactionEntity.getSum());
  }

  /**
   * {@inheritDoc} Throws BankAccountNotFoundException if either the source or destination account
   * is not found. Throws Exception for invalid transfer details.
   */
  @Transactional
  @Override
  public void addTransferToAccounts(TransferDto transferDto) {
    validateTransferDto(transferDto);

    TransferEntity transferEntity = transferMapper.mapFrom(transferDto);

    BankAccountEntity bankAccountEntitySentFrom =
        bankAccountRepository
            .findById(transferEntity.getSourceBankAccount().getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "The source account was not found."));
    BankAccountEntity bankAccountEntitySentTo =
        bankAccountRepository
            .findById(transferEntity.getDestinationBankAccount().getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "The destination account was not found."));

    transferRepository.save(transferEntity);

    bankAccountEntitySentFrom.addTransferSent(transferEntity);
    bankAccountEntitySentTo.addTransferReceived(transferEntity);

    updateBalanceOfAccount(bankAccountEntitySentFrom, -transferEntity.getSum());
    updateBalanceOfAccount(bankAccountEntitySentTo, transferEntity.getSum());
  }

  /** {@inheritDoc} */
  @Override
  public double getAverageExpenditureOnCategory(
      Long accountId, Long categoryId, int projectionDays) {
    LocalDateTime currentTime = LocalDateTime.now();

    double totalAmountUsedLastMonth =
        getAmountUsedOnTransactionsOnCategoryBetweenDate(
            accountId, categoryId, currentTime.minusDays(30), currentTime);
    return (totalAmountUsedLastMonth / 30) * projectionDays;
  }

  @Override
  public ProjectedDailyExpenseDto convertToProjectedExpenseDto(double projectedExpense) {
    return ProjectedDailyExpenseDto.builder().projectedExpense(projectedExpense).build();
  }

  /**
   * Checks if a category of a certain ID exists. If not it throws a CategoryNotFoundException.
   *
   * @param categoryId The ID being checked.
   */
  private void checkForCategoryNotFoundException(Long categoryId) {
    transactionCategoryRepository
        .findById(categoryId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Could not find Transaction category."));
  }

  /**
   * Updates the balance of an account.
   *
   * @param bankAccount The account that has its balance changed.
   * @param amount The amount that the balance is being changed by. Throws ResponseStatusException
   *     exception if an account's sum would be less than zero.
   */
  private void updateBalanceOfAccount(BankAccountEntity bankAccount, double amount) {
    double currentSum = bankAccount.getSum();

    double newSum = currentSum + amount;

    if (newSum < 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "A bank account's balance cannot be updated to be less than zero");
    }

    bankAccount.setSum(currentSum + amount);
  }

  /**
   * Validates the given BankAccountDto to ensure it is not already registered and has a non-null
   * ID.
   *
   * @param bankAccountDto the bank account DTO to validate.
   */
  private void validateBankAccountDto(BankAccountDto bankAccountDto) {
    if (checkAccountNumberAlreadyRegistered(bankAccountDto.getId())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "That account is already registered.");
    }

    if (bankAccountDto.getId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bank account ID cannot be null.");
    }
  }

  /**
   * Checks if an accountNumber already exists in the bank account repository.
   *
   * @param accountNumber The number being checked as an id.
   */
  private boolean checkAccountNumberAlreadyRegistered(Long accountNumber) {
    return bankAccountRepository.existsById(accountNumber);
  }

  /**
   * Validates the given TransactionDto to ensure it has no predetermined ID, has a non-null date,
   * and the category is valid.
   *
   * @param transactionDto the transaction DTO to validate.
   * @throws ResponseStatusException if the transaction ID is predeterminedly set or if the date is
   *     null.
   */
  private void validateTransactionDto(TransactionDto transactionDto) {
    checkForCategoryNotFoundException(transactionDto.getCategoryEntityId());

    if (transactionDto.getId() != null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Transaction id is auto-generated and cannot be predeterminedly set.");
    }

    if (transactionDto.getDate() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date cannot be null.");
    }
  }

  /**
   * Validates the given TransferDto to ensure it has no predetermined ID, has a non-null date, and
   * the amount is positive and non-zero.
   *
   * @param transferDto the transfer DTO to validate.
   * @throws ResponseStatusException if the transfer ID is predeterminedly set, the date is null,
   *     the transfer amount is zero, or the transfer amount is negative.
   */
  private void validateTransferDto(TransferDto transferDto) {
    if (transferDto.getId() != null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Transfer id is auto-generated and cannot be predeterminedly set.");
    }
    if (transferDto.getDate() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date cannot be null.");
    }

    if (transferDto.getSum() == 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer amount cannot be 0.");
    }

    if (transferDto.getSum() < 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Transfer amount cannot be less than 0.");
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param userId The user who the default bank accounts are added to.
   */
  @Transactional
  public void assignUserDefaultBankAccountSet(Long userId) {
    assignUserDefaultBankAccount(userId, DEFAULT_SAVING_ACCOUNT);
    assignUserDefaultBankAccount(userId, DEFAULT_BUFFER_ACCOUNT);
    assignUserDefaultBankAccount(userId, DEFAULT_CHECKING_ACCOUNT);
  }

  /**
   * Assigns a user a default bank account. Uses a unique bank account id as the account ID.
   *
   * @param userId The user which the accounts are added to.
   * @param accountName The name of the account created.
   * @return A dto with the data of the bank account created.
   */
  @Transactional
  protected BankAccountDto assignUserDefaultBankAccount(Long userId, String accountName) {
    BankAccountDto createdBankAccountDto =
        createDefaultAccountDto(userId, generateUniqueBankAccountId(), accountName);
    return createBankAccount(createdBankAccountDto);
  }

  /**
   * Creates a BankAccountDto with some default data and a random starting sum.
   *
   * @param userId The user which is to be the owner of the account.
   * @param accountNumber The ID of the account.
   * @param accountName The name of the account.
   * @return The generated BankAccountDto.
   */
  private BankAccountDto createDefaultAccountDto(
      Long userId, Long accountNumber, String accountName) {
    Random random = new Random();
    return BankAccountDto.builder()
        .id(accountNumber)
        .name(accountName)
        .sum(BASE_SUM + (random.nextInt(SUM_VARIATION_LIMIT) * SUM_VARIATION_MULTIPLIER))
        .userEntityId(userId)
        .build();
  }

  /**
   * Uses TestIdCounterService to generate a unique ID.
   *
   * @return The generated ID.
   */
  private Long generateUniqueBankAccountId() {
    Long accountId;
    do {
      accountId = testIdCounterService.getNextUniqueId() + BASE_ACCOUNT_ID;
    } while (checkAccountNumberAlreadyRegistered(accountId));

    return accountId;
  }
}
