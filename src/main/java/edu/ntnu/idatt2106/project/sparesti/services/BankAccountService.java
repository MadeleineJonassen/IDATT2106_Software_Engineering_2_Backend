package edu.ntnu.idatt2106.project.sparesti.services;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.ProjectedDailyExpenseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransferDto;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

/**
 * Service interface for managing bank accounts. Provides methods for creating bank accounts,
 * retrieving bank account information, adding transactions and transfers, and calculating
 * expenditures within specific categories over given time periods.
 */
public interface BankAccountService {
  /**
   * Creates a new bank account based on the provided data transfer object.
   *
   * @param bankAccountDto The data transfer object containing the information needed to create a
   *     new bank account.
   * @return The newly created BankAccountDto with updated information.
   */
  BankAccountDto createBankAccount(BankAccountDto bankAccountDto);

  /**
   * Calculates the total amount spent on transactions within a specific category over a specified
   * time period.
   *
   * @param bankAccountId The ID of the bank account.
   * @param categoryId The ID of the category for which transactions are to be totaled.
   * @param startDate The start date of the period for which to calculate the total amount.
   * @param endDate The end date of the period for which to calculate the total amount.
   * @return The total amount spent on the specified transactions.
   */
  double getAmountUsedOnTransactionsOnCategoryBetweenDate(
      Long bankAccountId, Long categoryId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Retrieves the details of a bank account by its ID.
   *
   * @param accountId The ID of the bank account to find.
   * @return A dto containing the information about the account.
   */
  BankAccountDto findOne(Long accountId);

  /**
   * Adds a transaction to a specified bank account.
   *
   * @param accountId The ID of the bank account to which the transaction will be added.
   * @param transactionDto The transaction data transfer object containing details about the
   *     transaction to be added.
   */
  void addTransactionToAccount(Long accountId, TransactionDto transactionDto);

  /**
   * Adds a transfer between two bank accounts.
   *
   * @param transferDto The transfer data transfer object containing details about the transfer.
   */
  void addTransferToAccounts(TransferDto transferDto);

  /**
   * Assigns a user some default bank accounts for testing purposes.
   *
   * @param userId The user who the default bank accounts are added to.
   */
  void assignUserDefaultBankAccountSet(Long userId);

  /**
   * Calculates the average daily expenditure on a specified category over a custom period defined
   * by the number of days provided. The average is computed based on the expenditure from the last
   * 30 days and then extrapolated to the number of days specified.
   *
   * @param accountId the ID of the account for which expenditures are being calculated.
   * @param categoryId the ID of the category for which expenditures are being calculated.
   * @param projectionDays the number of days over which the average should be extrapolated. This is
   *     not necessarily the last 'n' days but is used to scale the 30-day average.
   * @return the average expenditure for the specified number of days based on the last 30 days'
   *     data. The average is calculated by first determining the total expenditure in the last 30
   *     days, then dividing this total by 30 to get a daily average, which is then multiplied by
   *     'days' to project the average expenditure over the specified period.
   */
  double getAverageExpenditureOnCategory(Long accountId, Long categoryId, int projectionDays);

  /**
   * Converts a double value to a projectedExpenseDto.
   *
   * @param projectedExpense The double used as an attribute in the projectedExpenseDto.
   * @return The projectedExpenseDto.
   */
  ProjectedDailyExpenseDto convertToProjectedExpenseDto(double projectedExpense);
}
