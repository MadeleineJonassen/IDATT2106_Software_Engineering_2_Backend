package edu.ntnu.idatt2106.project.sparesti.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106.project.sparesti.controllers.BankAccountController;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.ProjectedDailyExpenseDto;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.BankAccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A test class for the BankAccountController, focusing on unit testing the handling of HTTP
 * requests and interactions with the service layer through mocked objects. It includes tests for
 * operations like adding transactions and fetching expected expenses for specific categories.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BankAccountControllerTest {

  @Mock
  private BankAccountService
      bankAccountService; // Mock of the BankAccountService for simulating service interactions.

  @Mock
  private UserInfoFromTokenService
      userInfoFromTokenService; // Mock of the UserInfoFromTokenService for user authentication
  // simulation.

  @InjectMocks
  private BankAccountController
      bankAccountController; // The controller under test with injected mocks.

  private MockMvc mockMvc; // Entry point for server-side Spring MVC test support.
  private ObjectMapper objectMapper =
      new ObjectMapper(); // Utility for converting objects to JSON strings.

  /**
   * Setup method to initialize the mockMvc object with the BankAccountController and configure
   * default return values for user authorization simulations.
   */
  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(bankAccountController).build();
    when(userInfoFromTokenService.validateUserAuthorization(
            any(HttpServletRequest.class), anyString()))
        .thenReturn(true);
  }

  /**
   * Test the addition of a transaction to an account through a POST request. This test checks the
   * response status and the content returned by the controller. It also verifies that the correct
   * service methods are called.
   *
   * @throws Exception if there's an issue with the mock MVC operations.
   */
  @Test
  public void testAddTransactionToAccount() throws Exception {
    TransactionDto transactionDto = new TransactionDto(); // Example data for the transaction.
    Long accountId = 1L;

    mockMvc
        .perform(
            post("/api/secure/accounts/{accountId}/transactions", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto)))
        .andExpect(status().isCreated())
        .andExpect(content().string("Transaction successfully added to the account."));

    verify(bankAccountService, times(1))
        .addTransactionToAccount(eq(accountId), any(TransactionDto.class));
  }

  /**
   * Test fetching the expected expense for a given account and category via a GET request. This
   * test verifies the HTTP status and JSON content of the response, and checks that user
   * authorization and service interactions are handled correctly.
   *
   * @throws Exception if there's an issue with the mock MVC operations.
   */
  @Test
  public void testGetExpectedExpense() throws Exception {
    Long accountId = 1L;
    Long categoryId = 1L;
    String username = "testUser";
    ProjectedDailyExpenseDto expectedExpense = new ProjectedDailyExpenseDto();
    expectedExpense.setProjectedExpense(150.00);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer token");

    when(userInfoFromTokenService.validateUserAuthorization(request, username)).thenReturn(true);
    when(bankAccountService.getAverageExpenditureOnCategory(accountId, categoryId, 1))
        .thenReturn(150.00);
    when(bankAccountService.convertToProjectedExpenseDto(150.00)).thenReturn(expectedExpense);

    mockMvc
        .perform(
            get(
                "/api/secure/accounts/{username}/{accountId}/{categoryId}/expected-expense",
                username,
                accountId,
                categoryId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.projectedExpense").value(150.00));

    verify(userInfoFromTokenService, times(1))
        .validateUserAuthorization(any(HttpServletRequest.class), eq(username));
    verify(bankAccountService, times(1)).getAverageExpenditureOnCategory(accountId, categoryId, 1);
    verify(bankAccountService, times(1)).convertToProjectedExpenseDto(150.00);
  }
}
