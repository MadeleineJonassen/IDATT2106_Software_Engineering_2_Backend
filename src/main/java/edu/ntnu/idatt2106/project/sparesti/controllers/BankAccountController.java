package edu.ntnu.idatt2106.project.sparesti.controllers;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.ProjectedDailyExpenseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionDto;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.BankAccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing bank account operations, particularly adding transactions to bank
 * accounts. This class interacts with the BankAccountService to perform business logic and
 * transaction management.
 */
@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/secure/accounts")
public class BankAccountController {

  private final BankAccountService bankAccountService;
  private final UserInfoFromTokenService userInfoFromTokenService;

  /**
   * Constructs a BankAccountController with a dependency on BankAccountService and the
   * userInfoFromTokenService.
   *
   * @param bankAccountService The service that handles the business logic for bank account
   *     operations
   * @param userInfoFromTokenService The service that validates tokens.
   */
  @Autowired
  public BankAccountController(
      BankAccountService bankAccountService, UserInfoFromTokenService userInfoFromTokenService) {
    this.bankAccountService = bankAccountService;
    this.userInfoFromTokenService = userInfoFromTokenService;
  }

  /**
   * Adds a transaction to a specified bank account using details provided in a TransactionDto. This
   * method handles POST requests to add a new transaction to an existing bank account.
   *
   * @param accountId The ID of the bank account to which the transaction is to be added. Must
   *     correspond to an existing account.
   * @param transactionDto The data transfer object containing all required details of the
   *     transaction to be added.
   * @return ResponseEntity containing a success message and HTTP status code if the transaction is
   *     successfully added, or an error message and status code if an error occurs.
   */
  @PostMapping("/{accountId}/transactions")
  public ResponseEntity<String> addTransactionToAccount(
      @PathVariable Long accountId, @RequestBody TransactionDto transactionDto) {
    bankAccountService.addTransactionToAccount(accountId, transactionDto);
    return new ResponseEntity<>(
        "Transaction successfully added to the account.", HttpStatus.CREATED);
  }

  /**
   * Retrieves the projected daily expense for a specific user, account, and category. This method
   * handles a GET request and returns a ResponseEntity containing the projected expense data.
   *
   * @param username the username to validate for data access.
   * @param accountId the ID of the bank account from which to fetch expense data.
   * @param categoryId the ID of the category for which to calculate expected expenses.
   * @param request the HttpServletRequest, used for authorization purposes.
   * @return a {@link ResponseEntity} containing a {@link ProjectedDailyExpenseDto} if the user is
   *     authorized, or a forbidden status if the user is not authorized to access the data.
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("/{username}/{accountId}/{categoryId}/expected-expense")
  public ResponseEntity<ProjectedDailyExpenseDto> getExpectedExpense(
      @PathVariable String username,
      @PathVariable Long accountId,
      @PathVariable Long categoryId,
      HttpServletRequest request) {

    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return new ResponseEntity<>(
        bankAccountService.convertToProjectedExpenseDto(
            bankAccountService.getAverageExpenditureOnCategory(accountId, categoryId, 1)),
        HttpStatus.OK);
  }
}
