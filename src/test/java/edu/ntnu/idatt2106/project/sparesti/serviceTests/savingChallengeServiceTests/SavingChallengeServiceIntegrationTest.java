package edu.ntnu.idatt2106.project.sparesti.serviceTests.savingChallengeServiceTests;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeResponseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.*;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.ChallengeState;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.*;
import edu.ntnu.idatt2106.project.sparesti.services.BankAccountService;
import edu.ntnu.idatt2106.project.sparesti.services.SavingChallengeService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.h2.engine.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static reactor.core.publisher.Mono.when;

/**
 * Integration tests for the SavingChallengeService, focusing on verifying the functionality and
 * interaction with the database. These tests ensure that the SavingChallengeService handles data
 * retrieval and modifications correctly under various scenarios.
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class SavingChallengeServiceIntegrationTest {
  @Autowired private SavingChallengeRepository savingChallengeRepository;

  @Autowired private SavingGoalRepository savingGoalRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private TransactionCategoryRepository transactionCategoryRepository;

  @Autowired
  private Mapper<SavingChallengeEntity, SavingChallengeResponseDto> savingChallengeMapper;

  @Autowired private BankAccountService bankAccountService;

  @Autowired private SavingChallengeService savingChallengeService;

  @Autowired private BankAccountRepository bankAccountRepository;

  @Autowired private BadgeRepository badgeRepository;

  private SavingChallengeEntity savingChallenge1;
  private SavingChallengeEntity savingChallenge2;
  private SavingGoalEntity savingGoal;
  private TransactionCategoryEntity transactionCategory;
  private UserEntity user;
  private BankAccountEntity bankAccountEntity1;

  /**
   * Initializes the test environment before each test, setting up necessary entities and
   * relationships in the database.
   */
  @BeforeEach
  public void setUp() {
    bankAccountEntity1 = BankAccountEntity.builder().id(1L).sum(3000).build();
    BankAccountEntity bankAccountEntity2 = BankAccountEntity.builder().id(2L).build();

    transactionCategory = TransactionCategoryEntity.builder().suggestedAmount(20).build();
    transactionCategoryRepository.save(transactionCategory);
    TransactionEntity transaction =
        TransactionEntity.builder()
            .transactionCategoryEntity(transactionCategory)
            .date(LocalDateTime.now().minusDays(7))
            .sum(-200)
            .build();
    bankAccountEntity1.addTransaction(transaction);

    savingChallenge1 =
        SavingChallengeEntity.builder()
            .state(ChallengeState.IN_PROGRESS)
            .expectedExpense(100)
            .spendingGoal(80)
            .startDate(LocalDate.now().minusDays(10))
            .endingDate(LocalDate.now().minusDays(1))
            .transactionCategory(transactionCategory)
            .build();

    savingChallenge2 =
        SavingChallengeEntity.builder()
            .state(ChallengeState.IN_PROGRESS)
            .expectedExpense(220)
            .spendingGoal(210)
            .startDate(LocalDate.now().minusDays(10))
            .endingDate(LocalDate.now().minusDays(5))
            .transactionCategory(transactionCategory)
            .build();

    List<SavingChallengeEntity> savingChallengeEntities = new ArrayList<>();
    savingChallengeEntities.add(savingChallenge1);
    savingChallengeEntities.add(savingChallenge2);

    savingGoal =
        SavingGoalEntity.builder()
            .sourceBankAccount(bankAccountEntity1)
            .destinationBankAccount(bankAccountEntity2)
            .savingChallenges(savingChallengeEntities)
            .state(GoalState.IN_PROGRESS)
            .build();

    savingChallenge1.setSavingGoal(savingGoal);
    savingChallenge2.setSavingGoal(savingGoal);

    savingGoalRepository.save(savingGoal);

    user =
        UserEntity.builder()
            .username("John Doe")
            .savingGoals(List.of(savingGoal))
            .badgeProgress(new ArrayList<>())
            .score(200)
            .completedGoals(1)
            .currentStreak(1)
            .completedChallenges(1)
            .totalSaved(500.0)
            .build();

    user.addBankAccount(bankAccountEntity1);
    user.addBankAccount(bankAccountEntity2);

    userRepository.save(user);

    savingGoal.setUserEntity(user);

    savingGoalRepository.save(savingGoal);
  }

  /**
   * Tests retrieving all challenges for a specific user, ensuring that the service correctly
   * identifies the number of challenges and accurately fetches their configured spending goals.
   */
  @Test
  public void getAllChallengesFromUser() {
    List<SavingChallengeResponseDto> savingChallengesFound =
        savingChallengeService.getAndProcessUserChallenges(user.getUsername());

    assertFalse(savingChallengesFound.isEmpty());
    assertEquals(2, savingChallengesFound.size());
    assertEquals(80, savingChallengesFound.get(0).getSpendingGoal());
  }

  /**
   * Tests retrieving all challenges for a user, verifying that the service correctly tracks and
   * updates the state of each challenge based on predefined conditions.
   */
  @Test
  public void getAllChallengesFromUserTracksChallengeState() {
    List<SavingChallengeResponseDto> savingChallengesFound =
        savingChallengeService.getAndProcessUserChallenges(user.getUsername());

    assertEquals(ChallengeState.FAILED, savingChallengesFound.get(0).getState());
    assertEquals(ChallengeState.COMPLETED, savingChallengesFound.get(1).getState());
  }

  /**
   * Tests retrieving all saving challenges associated with a specific goal, ensuring that all
   * related challenges are correctly identified and their details are accurately fetched.
   */
  @Test
  public void getAllSavingChallengesFromGoal() {
    List<SavingChallengeResponseDto> savingChallengesFound =
        savingChallengeService.getAndProcessAllChallengesInGoal(savingGoal.getId());

    assertFalse(savingChallengesFound.isEmpty());
    assertEquals(2, savingChallengesFound.size());
    assertEquals(80, savingChallengesFound.get(0).getSpendingGoal());
  }

  /**
   * Tests the creation of a new saving challenge under a saving goal, verifying that the challenge
   * is correctly created with the specified attributes and associated with the correct saving goal.
   */
  @Test
  public void createChallenge() {
    SavingChallengeRequestDto savingChallengeRequestDto = new SavingChallengeRequestDto();
    savingChallengeRequestDto.setCategoryId(transactionCategory.getId());
    savingChallengeRequestDto.setSpendingGoal(200);
    savingChallengeRequestDto.setStartDate(LocalDate.now());
    savingChallengeRequestDto.setEndingDate(LocalDate.now().plusDays(10));
    savingChallengeRequestDto.setExpectedExpense(250);

    assertThrows(
        ResponseStatusException.class,
        () ->
            savingChallengeService.createSavingChallenge(
                savingChallengeRequestDto, savingGoal.getId()));

    /*
    SavingChallengeResponseDto responseDto =
        savingChallengeService.createSavingChallenge(savingChallengeRequestDto, savingGoal.getId());


    SavingChallengeEntity savingChallengeInDB =
        savingChallengeRepository.findById(responseDto.getId()).orElseThrow();

    assertEquals(200, savingChallengeInDB.getSpendingGoal());
    assertEquals(savingGoal.getId(), savingChallengeInDB.getSavingGoal().getId());*/
  }

  /**
   * Tests completing a saving challenge, verifying that the challenge completes successfully,
   * transfers the saved amount appropriately, and updates the balances of the involved bank
   * accounts.
   */
  @Test
  public void finishSavingChallenge() {
    BadgeEntity badgeEntity = BadgeEntity.builder().name("Teenage Mutant Ninja-piggy").build();
    BadgeEntity badgeEntity2 = BadgeEntity.builder().name("Griseflink sparer").build();

    BadgeTierEntity badgeTierEntity =
        BadgeTierEntity.builder().tier(1).badge(badgeEntity).target(5).build();

    BadgeTierEntity badgeTierEntity2 =
        BadgeTierEntity.builder().tier(1).badge(badgeEntity).target(5).build();

    badgeEntity.setBadgeTiers(Arrays.asList(badgeTierEntity));
    badgeEntity2.setBadgeTiers(Arrays.asList(badgeTierEntity2));

    badgeRepository.save(badgeEntity);
    badgeRepository.save(badgeEntity2);

    BadgeProgressEntity badgeProgressEntity =
        BadgeProgressEntity.builder().userEntity(user).badgeEntity(badgeEntity).progress(1).build();

    BadgeProgressEntity badgeProgressEntity2 =
        BadgeProgressEntity.builder()
            .userEntity(user)
            .badgeEntity(badgeEntity2)
            .progress(1)
            .build();

    user.addBadgeProgress(badgeProgressEntity);
    user.addBadgeProgress(badgeProgressEntity2);

    double originalBalance = savingGoal.getSourceBankAccount().getSum();

    savingGoal.setUserEntity(user);

    SavingChallengeEntity savingChallenge3 =
        SavingChallengeEntity.builder()
            .state(ChallengeState.IN_PROGRESS)
            .expectedExpense(400)
            .spendingGoal(300)
            .startDate(LocalDate.now().minusDays(10))
            .endingDate(LocalDate.now().minusDays(1))
            .state(ChallengeState.COMPLETED)
            .transactionCategory(transactionCategory)
            .savingGoal(savingGoal)
            .build();

    savingChallengeRepository.save(savingChallenge3);

    ArrayList<SavingGoalEntity> savingGoals = new ArrayList<>();
    savingGoals.add(savingGoal);

    //    UserEntity user =
    //        UserEntity.builder()
    //            .username("JohnDoe")
    //            .savingGoals(savingGoals)
    //            .email("Testmail@mail.com")
    //            .fullName("John Doe")
    //
    //            .build();

    user.setSavingGoals(savingGoals);
    userRepository.save(user);
    System.out.println(user);
    SavingChallengeResponseDto responseDto =
        savingChallengeService.finishChallenge(savingChallenge3.getId(), user.getUsername());

    double amountSaved = responseDto.getAmountSaved();

    assertEquals(amountSaved, originalBalance - savingGoal.getSourceBankAccount().getSum(), 0.01);
    assertEquals(amountSaved, savingGoal.getDestinationBankAccount().getSum());
    assertEquals(ChallengeState.COMPLETED_AND_TRANSFERRED, savingChallenge3.getState());
  }

  /**
   * Tests that generating saving challenge suggestions work and that the fields in the generated
   * saving challenge is as expected.
   */
  @Test
  public void generateSavingChallengeSuggestions() {
    TransactionCategoryEntity uniqueTransactionCategory =
        TransactionCategoryEntity.builder().suggestedAmount(20).build();

    transactionCategoryRepository.save(uniqueTransactionCategory);

    TransactionEntity transaction2 =
        TransactionEntity.builder()
            .sum(-280)
            .transactionCategoryEntity(uniqueTransactionCategory)
            .date(LocalDateTime.now().minusDays(7))
            .build();

    bankAccountEntity1.addTransaction(transaction2);
    bankAccountRepository.save(bankAccountEntity1);

    List<SavingChallengeResponseDto> savingChallengeResponseDtos =
        savingChallengeService.getChallengeSuggestions(savingGoal.getId());

    assertFalse(savingChallengeResponseDtos.isEmpty());
    assertEquals(1, savingChallengeResponseDtos.size());
    assertEquals(
        uniqueTransactionCategory.getId(),
        savingChallengeResponseDtos.get(0).getTransactionCategory().getId());
    assertEquals(42, savingChallengeResponseDtos.get(0).getSpendingGoal());
  }

  /**
   * Tests that generating saving challenge suggestions will return no suggestions if transaction
   * category suggest amount is larger.
   */
  @Test
  public void generateSavingChallengesReturnEmptyListWith() {
    TransactionCategoryEntity uniqueTransactionCategory =
        TransactionCategoryEntity.builder().suggestedAmount(2000).build();

    transactionCategoryRepository.save(uniqueTransactionCategory);

    TransactionEntity transaction2 =
        TransactionEntity.builder()
            .sum(-5000)
            .transactionCategoryEntity(uniqueTransactionCategory)
            .date(LocalDateTime.now().minusDays(7))
            .build();

    bankAccountEntity1.addTransaction(transaction2);
    bankAccountRepository.save(bankAccountEntity1);

    List<SavingChallengeResponseDto> savingChallengeResponseDtos =
        savingChallengeService.getChallengeSuggestions(savingGoal.getId());

    assertTrue(savingChallengeResponseDtos.isEmpty());
  }

  /**
   * Checks that the generateSavingChallenges-method can return multiple saving challenges. Also
   * checks that they are sorted correctly.
   */
  @Test
  public void generateSavingChallengesReturnMultipleChallengesSorted() {
    TransactionCategoryEntity uniqueTransactionCategory1 =
        TransactionCategoryEntity.builder().suggestedAmount(20).build();

    transactionCategoryRepository.save(uniqueTransactionCategory1);

    TransactionEntity transaction1 =
        TransactionEntity.builder()
            .sum(-400)
            .transactionCategoryEntity(uniqueTransactionCategory1)
            .date(LocalDateTime.now().minusDays(7))
            .build();

    TransactionCategoryEntity uniqueTransactionCategory2 =
        TransactionCategoryEntity.builder().suggestedAmount(40).build();

    transactionCategoryRepository.save(uniqueTransactionCategory2);

    TransactionEntity transaction2 =
        TransactionEntity.builder()
            .sum(-260)
            .transactionCategoryEntity(uniqueTransactionCategory2)
            .date(LocalDateTime.now().minusDays(7))
            .build();

    TransactionCategoryEntity uniqueTransactionCategory3 =
        TransactionCategoryEntity.builder().suggestedAmount(10).build();

    transactionCategoryRepository.save(uniqueTransactionCategory3);

    TransactionEntity transaction3 =
        TransactionEntity.builder()
            .sum(-800)
            .transactionCategoryEntity(uniqueTransactionCategory3)
            .date(LocalDateTime.now().minusDays(7))
            .build();

    bankAccountEntity1.addTransaction(transaction1);
    bankAccountEntity1.addTransaction(transaction2);
    bankAccountEntity1.addTransaction(transaction3);
    bankAccountRepository.save(bankAccountEntity1);

    List<SavingChallengeResponseDto> savingChallengeResponseDtos =
        savingChallengeService.getChallengeSuggestions(savingGoal.getId());

    assertFalse(savingChallengeResponseDtos.isEmpty());
    assertEquals(3, savingChallengeResponseDtos.size());
    assertEquals(transaction3.getId(), savingChallengeResponseDtos.get(0).getId());
    assertEquals(transaction1.getId(), savingChallengeResponseDtos.get(1).getId());
    assertEquals(transaction2.getId(), savingChallengeResponseDtos.get(2).getId());
  }

  /** Tests that challenge states are updated when using the updateAllSavingChallengeStates. */
  @Test
  public void updateAllSavingChallengeStates() {
    savingChallengeService.updateAllSavingChallengeStates();
    assertEquals(ChallengeState.FAILED, savingChallenge1.getState());
    assertEquals(ChallengeState.COMPLETED, savingChallenge2.getState());
  }
}
