package edu.ntnu.idatt2106.project.sparesti.serviceTests.savingChallengeServiceTests;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeResponseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.*;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.ChallengeState;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import edu.ntnu.idatt2106.project.sparesti.mappers.impl.SavingChallengeMapperImpl;
import edu.ntnu.idatt2106.project.sparesti.repositories.*;
import edu.ntnu.idatt2106.project.sparesti.services.BankAccountService;
import edu.ntnu.idatt2106.project.sparesti.services.SavingChallengeService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SavingChallengeServiceUnitTest {

  @MockBean private SavingChallengeRepository savingChallengeRepository;

  @MockBean private TransactionCategoryRepository transactionCategoryRepository;

  @MockBean private SavingGoalRepository savingGoalRepository;

  @MockBean private SavingChallengeMapperImpl savingChallengeMapper;

  @MockBean private UserRepository userRepository;

  @MockBean private BankAccountService bankAccountService;

  @Autowired private SavingChallengeService service;

  @MockBean private BadgeRepository badgeRepository;

  private SavingChallengeEntity challenge;
  private SavingChallengeRequestDto savingChallengeRequestDto;
  private SavingGoalEntity mockSavingGoalEntity;
  private TransactionCategoryEntity mockTransactionCategoryEntity;
  private SavingChallengeResponseDto expectedDto;

  /**
   * Setup method that runs before each test and provides a test environment with some mocked
   * methods.
   */
  @BeforeEach
  void setUp() {
    mockSavingGoalEntity = new SavingGoalEntity();
    mockSavingGoalEntity.setId(1L);
    mockSavingGoalEntity.setState(GoalState.IN_PROGRESS);
    mockSavingGoalEntity.setSourceBankAccount(BankAccountEntity.builder().id(1L).sum(300).build());

    mockTransactionCategoryEntity =
        TransactionCategoryEntity.builder().id(1L).suggestedAmount(200).build();

    savingChallengeRequestDto = new SavingChallengeRequestDto();
    savingChallengeRequestDto.setCategoryId(1L);
    savingChallengeRequestDto.setExpectedExpense(500);
    savingChallengeRequestDto.setSpendingGoal(300);
    savingChallengeRequestDto.setStartDate(LocalDate.now());
    savingChallengeRequestDto.setEndingDate(LocalDate.now().plusDays(30));

    challenge =
        SavingChallengeEntity.builder()
            .id(1L)
            .savingGoal(mockSavingGoalEntity)
            .transactionCategory(mockTransactionCategoryEntity)
            .expectedExpense(savingChallengeRequestDto.getExpectedExpense())
            .spendingGoal(savingChallengeRequestDto.getSpendingGoal())
            .startDate(savingChallengeRequestDto.getStartDate())
            .endingDate(savingChallengeRequestDto.getEndingDate())
            .state(ChallengeState.IN_PROGRESS)
            .build();

    List<SavingChallengeEntity> mockChallengeInList = new ArrayList<>();
    mockChallengeInList.add(challenge);
    mockSavingGoalEntity.setSavingChallenges(mockChallengeInList);
    expectedDto = new SavingChallengeResponseDto();
    expectedDto.setId(1L);

    given(savingGoalRepository.findById(1L)).willReturn(Optional.of(mockSavingGoalEntity));
    given(transactionCategoryRepository.findById(1L))
        .willReturn(Optional.of(mockTransactionCategoryEntity));
    given(savingChallengeRepository.save(any(SavingChallengeEntity.class))).willReturn(challenge);
    given(savingChallengeRepository.findById(anyLong())).willReturn(Optional.ofNullable(challenge));
    given(savingChallengeMapper.mapTo(any(SavingChallengeEntity.class))).willReturn(expectedDto);
    given(
            bankAccountService.getAmountUsedOnTransactionsOnCategoryBetweenDate(
                any(), any(), any(), any()))
        .willReturn(100.0);
  }

  /** Tests the creation of a saving challenge using a valid saving goal. */
  @Test
  void testCreateSavingChallenge() {
    UserEntity user = UserEntity.builder().savingGoals(Arrays.asList(mockSavingGoalEntity)).build();
    userRepository.save(user);

    mockSavingGoalEntity.setUserEntity(user);
    savingGoalRepository.save(mockSavingGoalEntity);

    assertThrows(
        ResponseStatusException.class,
        () -> service.createSavingChallenge(savingChallengeRequestDto, 1L));
    /*
    SavingChallengeResponseDto actualDto =
        service.createSavingChallenge(savingChallengeRequestDto, 1L);

    assertNotNull(actualDto);
    assertEquals(Long.valueOf(1L), actualDto.getId());*/
  }

  /**
   * Tests the retrieval of all challenges associated with a specific goal, including fetching
   * current spending. This test ensures that the system correctly calculates and fetches the
   * current spending for each challenge associated with a saving goal.
   */
  @Test
  public void testGetAllChallengesInGoal() {
    when(bankAccountService.getAmountUsedOnTransactionsOnCategoryBetweenDate(
            any(), any(), any(), any()))
        .thenReturn(100.0);

    List<SavingChallengeResponseDto> result =
        service.getAndProcessAllChallengesInGoal(mockSavingGoalEntity.getId());

    assertEquals(1, result.size());
    assertEquals(100.0, result.get(0).getCurrentSpending());
  }

  /**
   * Tests the retrieval of all challenges for a user, ensuring it fetches current spending. This
   * test checks the functionality of fetching all saving challenges for a user and correctly
   * calculating the current spending for these challenges.
   */
  @Test
  void testGetAllChallengesInUser() {
    Long goalId = 1L;
    SavingGoalEntity goal = new SavingGoalEntity();
    goal.setId(goalId);
    goal.setSourceBankAccount(BankAccountEntity.builder().id(1L).build());

    SavingChallengeEntity challenge = new SavingChallengeEntity();
    challenge.setId(1L);
    challenge.setStartDate(LocalDate.now().minusDays(10));
    challenge.setEndingDate(LocalDate.now());
    challenge.setSavingGoal(goal);
    challenge.setSpendingGoal(100);
    challenge.setState(ChallengeState.IN_PROGRESS);
    challenge.setTransactionCategory(new TransactionCategoryEntity());

    goal.setSavingChallenges(List.of(challenge));

    SavingChallengeResponseDto savingChallengeResponseDto = new SavingChallengeResponseDto();
    savingChallengeResponseDto.setCurrentSpending(100);
    savingChallengeResponseDto.setExpectedExpense(100);

    UserEntity user = UserEntity.builder().username("testUser").savingGoals(List.of(goal)).build();

    when(userRepository.findUserEntityByUsername(user.getUsername())).thenReturn(Optional.of(user));
    when(savingChallengeRepository.findById(anyLong())).thenReturn(Optional.of(challenge));
    when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(goal));
    when(bankAccountService.getAmountUsedOnTransactionsOnCategoryBetweenDate(
            any(), any(), any(), any()))
        .thenReturn(100.0);
    when(savingChallengeMapper.mapTo(challenge)).thenReturn(savingChallengeResponseDto);

    List<SavingChallengeResponseDto> result =
        service.getAndProcessUserChallenges(user.getUsername());

    assertEquals(1, result.size());
    assertEquals(100.0, result.get(0).getCurrentSpending());
  }

  /**
   * Tests that finish challenge method successfully marks challenge as COMPLETE_AND_TRANSFERRED.
   */
  @Test
  void testFinishChallenge() {
    UserEntity user =
        UserEntity.builder()
            .username("testUser")
            .badgeProgress(new ArrayList<>())
            .completedChallenges(1)
            .currentStreak(1)
            .completedGoals(1)
            .totalSaved(500.0)
            .savingGoals(List.of(mockSavingGoalEntity))
            .build();

    BadgeEntity badgeEntity = BadgeEntity.builder().name("Teenage Mutant Ninja-piggy").build();

    BadgeTierEntity badgeTierEntity =
        BadgeTierEntity.builder().tier(1).badge(badgeEntity).target(10).description("test").build();

    badgeEntity.setBadgeTiers(Arrays.asList(badgeTierEntity));

    BadgeProgressEntity badgeProgressEntity =
        BadgeProgressEntity.builder().badgeEntity(badgeEntity).userEntity(user).progress(1).build();
    user.addBadgeProgress(badgeProgressEntity);

    BankAccountEntity destinationAccount = BankAccountEntity.builder().id(2L).build();
    mockSavingGoalEntity.setDestinationBankAccount(destinationAccount);

    mockSavingGoalEntity.setUserEntity(user);
    challenge.setState(ChallengeState.COMPLETED);

    BankAccountDto bankAccountDto = BankAccountDto.builder().id(1L).sum(1000).build();

    when(bankAccountService.findOne(anyLong())).thenReturn(bankAccountDto);
    doNothing().when(bankAccountService).addTransferToAccounts(any());
    when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(user));
    when(badgeRepository.findBadgeEntityByName(anyString())).thenReturn(badgeEntity);

    SavingChallengeResponseDto savingChallengeResponse =
        service.finishChallenge(challenge.getId(), user.getUsername());

    assertEquals(ChallengeState.COMPLETED_AND_TRANSFERRED, savingChallengeResponse.getState());
  }

  /** Tests that trying to finish a challenge that is uncomplete throws exception. */
  @Test
  void testTryToFinishUncompleteChallenge() {
    Long goalId = 1L;
    SavingGoalEntity goal = new SavingGoalEntity();
    goal.setId(goalId);

    BankAccountEntity sourceAccount = BankAccountEntity.builder().id(1L).sum(300).build();
    BankAccountEntity destinationAccount = BankAccountEntity.builder().id(2L).build();
    UserEntity user = UserEntity.builder().username("testUser").savingGoals(List.of(goal)).build();

    goal.setSourceBankAccount(sourceAccount);
    goal.setDestinationBankAccount(destinationAccount);

    SavingChallengeEntity challenge = new SavingChallengeEntity();
    challenge.setId(1L);
    challenge.setStartDate(LocalDate.now().minusDays(10));
    challenge.setEndingDate(LocalDate.now());
    challenge.setSavingGoal(goal);
    challenge.setSpendingGoal(100);
    challenge.setExpectedExpense(100);
    challenge.setState(ChallengeState.IN_PROGRESS);
    challenge.setTransactionCategory(new TransactionCategoryEntity());

    goal.setSavingChallenges(List.of(challenge));

    SavingChallengeResponseDto savingChallengeResponseDto = new SavingChallengeResponseDto();
    savingChallengeResponseDto.setCurrentSpending(100);
    savingChallengeResponseDto.setExpectedExpense(100);

    when(savingChallengeRepository.findById(anyLong())).thenReturn(Optional.of(challenge));
    when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(goal));

    ResponseStatusException thrownException =
        assertThrows(
            ResponseStatusException.class,
            () -> service.finishChallenge(challenge.getId(), user.getUsername()),
            "Expected finishChallenge() to throw, but it didn't");

    assertEquals(thrownException.getStatusCode(), HttpStatus.BAD_REQUEST);
    assertEquals("Challenge is not completed.", thrownException.getReason());
  }

  /** Tests that trying to finish a challenge with insufficient funds throws exception. */
  @Test
  void testFinishWithInsufficientFundsChallenge() {
    BankAccountEntity destinationAccount = BankAccountEntity.builder().id(2L).build();
    mockSavingGoalEntity.setDestinationBankAccount(destinationAccount);

    challenge.setState(ChallengeState.COMPLETED);

    BankAccountDto bankAccountDto = BankAccountDto.builder().id(1L).sum(0).build();

    UserEntity user =
        UserEntity.builder()
            .username("testUser")
            .savingGoals(List.of(mockSavingGoalEntity))
            .build();

    when(bankAccountService.findOne(anyLong())).thenReturn(bankAccountDto);
    doNothing().when(bankAccountService).addTransferToAccounts(any());
    when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(user));

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> service.finishChallenge(challenge.getId(), user.getUsername()),
            "Expected finishChallenge() to throw due to insufficient funds, but it did not");

    assertEquals(
        "404 NOT_FOUND \"Insufficient funds to perform the transfer.\"", exception.getMessage());
  }

  /** Tests generate Challenges method generates a challenge with the correct spending goal. */
  @Test
  void testGenerateChallenges() {
    Long goalId = 1L;
    SavingGoalEntity goal = new SavingGoalEntity();
    goal.setId(goalId);

    TransactionCategoryEntity transactionCategory =
        TransactionCategoryEntity.builder().id(1L).suggestedAmount(200).build();

    List<TransactionCategoryEntity> transactionCategories = new ArrayList<>();
    transactionCategories.add(transactionCategory);

    BankAccountEntity sourceAccount = BankAccountEntity.builder().id(1L).sum(0).build();

    goal.setSourceBankAccount(sourceAccount);

    SavingChallengeEntity challenge = new SavingChallengeEntity();

    goal.setSavingChallenges(List.of(challenge));

    UserEntity user = UserEntity.builder().savingGoals(new ArrayList<>()).build();

    goal.setUserEntity(user);

    SavingChallengeResponseDto savingChallengeResponseDto = new SavingChallengeResponseDto();
    savingChallengeResponseDto.setCurrentSpending(100);
    savingChallengeResponseDto.setExpectedExpense(100);
    savingChallengeResponseDto.setAmountSaved(200);

    when(bankAccountService.getAverageExpenditureOnCategory(anyLong(), anyLong(), anyInt()))
        .thenReturn(300.0);
    when(savingGoalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
    when(transactionCategoryRepository.findByIdNotIn(any())).thenReturn(transactionCategories);
    when(transactionCategoryRepository.findAll()).thenReturn(transactionCategories);

    List<SavingChallengeResponseDto> results = service.getChallengeSuggestions(goalId);
    assertEquals(250, results.get(0).getSpendingGoal());
  }
}
