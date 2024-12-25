package edu.ntnu.idatt2106.project.sparesti.serviceTests.savingGoalServiceTests;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoGeneralResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoPost;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.*;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingChallengeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingGoalEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.ChallengeState;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.BadgeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.BankAccountRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.SavingGoalRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.SavingGoalService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class SavingGoalServiceIntegrationTest {
  @Autowired private SavingGoalService savingGoalService;

  @Autowired private SavingGoalRepository savingGoalRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private BankAccountRepository bankAccountRepository;

  @Autowired private BadgeRepository badgeRepository;

  @Autowired private Mapper<SavingGoalEntity, SavingGoalDtoGeneralResponse> savingGoalMapper;

  private BankAccountEntity bankAccountEntity1;
  private BankAccountEntity bankAccountEntity2;
  private UserEntity user;

  /**
   * Sets up the test environment before each test. This includes creating user and bank account
   * entities and saving them using their respective repositories.
   */
  @BeforeEach
  public void setUp() {
    bankAccountEntity1 = BankAccountEntity.builder().id(1L).build();
    bankAccountEntity2 = BankAccountEntity.builder().id(2L).build();

    List<BankAccountEntity> userBankAccounts = new ArrayList<>();
    userBankAccounts.add(bankAccountEntity1);
    userBankAccounts.add(bankAccountEntity2);

    BadgeProgressEntity badgeProgress = new BadgeProgressEntity();
    List<BadgeProgressEntity> badgeProgressList = new ArrayList<>();
    badgeProgressList.add(badgeProgress);

    BadgeEntity badgeEntity = BadgeEntity.builder().name("Supersparings-gris").build();

    BadgeEntity badgeEntity2 = BadgeEntity.builder().name("Svinerik").build();

    BadgeTierEntity badgeTierEntity2 =
        BadgeTierEntity.builder()
            .tier(1)
            .badge(badgeEntity2)
            .target(20)
            .description("Test tier")
            .build();

    BadgeTierEntity badgeTierEntity =
        BadgeTierEntity.builder()
            .tier(1)
            .badge(badgeEntity)
            .target(20)
            .description("Test tier")
            .build();

    badgeEntity.setBadgeTiers(Arrays.asList(badgeTierEntity));
    badgeEntity2.setBadgeTiers(Arrays.asList(badgeTierEntity2));

    badgeRepository.save(badgeEntity);
    badgeRepository.save(badgeEntity2);

    BadgeProgressEntity badgeProgressEntity =
        BadgeProgressEntity.builder().userEntity(user).progress(1).badgeEntity(badgeEntity).build();

    BadgeProgressEntity badgeProgressEntity2 =
        BadgeProgressEntity.builder()
            .userEntity(user)
            .progress(1)
            .badgeEntity(badgeEntity2)
            .build();

    user =
        UserEntity.builder()
            .username("John Doe")
            .bankAccounts(userBankAccounts)
            .currentStreak(0)
            .completedChallenges(1)
            .completedGoals(1)
            .build();

    user.addBadgeProgress(badgeProgressEntity);

    userRepository.save(user);
  }

  /**
   * Tests the creation of a saving goal. Ensures that the newly created saving goal is not null and
   * exists in the repository.
   */
  @Test
  public void createSavingGoal() {
    SavingGoalDtoPost savingGoalDtoPost = new SavingGoalDtoPost();
    savingGoalDtoPost.setSourceBankAccountId(bankAccountEntity1.getId());
    savingGoalDtoPost.setDestinationBankAccountId(bankAccountEntity2.getId());
    savingGoalDtoPost.setGoalSum(200);
    savingGoalDtoPost.setEndingDate(LocalDate.now());
    savingGoalDtoPost.setTitle("Title");

    SavingGoalDtoGeneralResponse savingGoalResponse =
        savingGoalService.createSavingGoal(savingGoalDtoPost, user.getUsername());

    assertNotNull(savingGoalResponse);
    assertTrue(savingGoalRepository.existsById(savingGoalResponse.getId()));
  }

  /**
   * Tests the editing of a saving goal. Verifies that changes to title and goal sum are correctly
   * updated in the database.
   */
  @Test
  public void editSavingGoal() {
    String editedTitle = "Edited Title";
    Integer editedSum = 10;

    SavingGoalEntity savingGoalEntity =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .endingDate(LocalDate.now().plusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .title("Title")
            .build();

    savingGoalRepository.save(savingGoalEntity);
    List<SavingGoalEntity> savingGoals = new ArrayList<>();
    savingGoals.add(savingGoalEntity);
    user.setSavingGoals(savingGoals);
    userRepository.save(user);

    SavingGoalDtoPost savingGoalDtoPost = new SavingGoalDtoPost();
    savingGoalDtoPost.setTitle(editedTitle);
    savingGoalDtoPost.setGoalSum(editedSum);

    SavingGoalDtoGeneralResponse savingGoalDtoResponse =
        savingGoalService.editSavingGoal(
            savingGoalDtoPost, user.getUsername(), savingGoalEntity.getId());

    assertNotNull(savingGoalDtoResponse);
    assertEquals(editedTitle, savingGoalEntity.getTitle());
    assertEquals(editedSum, savingGoalEntity.getGoalSum());
  }

  /**
   * Tests retrieval of all saving goals for a specific user. Ensures that the correct number of
   * saving goals is returned for the user.
   */
  @Test
  public void getAllSavingGoalsForUser() {
    SavingGoalEntity savingGoalEntity1 =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .endingDate(LocalDate.now().plusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .title("Title")
            .build();

    SavingGoalEntity savingGoalEntity2 =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .endingDate(LocalDate.now().plusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .title("Title")
            .build();

    savingGoalRepository.save(savingGoalEntity1);
    savingGoalRepository.save(savingGoalEntity2);

    List<SavingGoalEntity> savingGoals = new ArrayList<>();

    savingGoals.add(savingGoalEntity1);
    savingGoals.add(savingGoalEntity2);

    user.setSavingGoals(savingGoals);
    userRepository.save(user);

    List<SavingGoalDtoGeneralResponse> foundSavingGoals =
        savingGoalService.getAllSavingGoalsForUser(user.getUsername());

    assertEquals(2, foundSavingGoals.size());
  }

  @Test
  public void getAllSavingGoalsForUserWhileCheckingState() {
    SavingGoalEntity savingGoalEntity1 =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .endingDate(LocalDate.now().plusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .title("Title")
            .build();

    SavingGoalEntity savingGoalEntity2 =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .endingDate(LocalDate.now().plusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .title("Title")
            .build();

    savingGoalRepository.save(savingGoalEntity1);
    savingGoalRepository.save(savingGoalEntity2);

    List<SavingGoalEntity> savingGoals = new ArrayList<>();

    savingGoals.add(savingGoalEntity1);
    savingGoals.add(savingGoalEntity2);

    user.setSavingGoals(savingGoals);
    userRepository.save(user);

    List<SavingGoalDtoGeneralResponse> foundSavingGoals =
        savingGoalService.getAllSavingGoalsForUser(user.getUsername());

    assertEquals(2, foundSavingGoals.size());
  }

  @Test
  public void getAllSavingGoalsForUserUpdatesChallengeState() {
    SavingChallengeEntity savingChallenge1 =
        SavingChallengeEntity.builder().state(ChallengeState.IN_PROGRESS).build();
    SavingChallengeEntity savingChallenge2 =
        SavingChallengeEntity.builder().state(ChallengeState.IN_PROGRESS).build();

    List<SavingChallengeEntity> savingChallenges1 = new ArrayList<>();
    savingChallenges1.add(savingChallenge1);

    List<SavingChallengeEntity> savingChallenges2 = new ArrayList<>();
    savingChallenges2.add(savingChallenge2);

    SavingGoalEntity savingGoalEntity1 =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .amountSaved(300.0)
            .endingDate(LocalDate.now().plusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .title("Title")
            .userEntity(user)
            .savingChallenges(savingChallenges1)
            .build();

    SavingGoalEntity savingGoalEntity2 =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .amountSaved(100.0)
            .endingDate(LocalDate.now().minusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .userEntity(user)
            .title("Title")
            .savingChallenges(savingChallenges2)
            .build();

    savingGoalRepository.save(savingGoalEntity1);
    savingGoalRepository.save(savingGoalEntity2);

    List<SavingGoalEntity> savingGoals = new ArrayList<>();

    savingGoals.add(savingGoalEntity1);
    savingGoals.add(savingGoalEntity2);

    user.setSavingGoals(savingGoals);
    userRepository.save(user);

    savingGoalService.getAllSavingGoalsForUser(user.getUsername());

    SavingGoalEntity savingGoal1Result =
        savingGoalRepository.findById(savingGoalEntity1.getId()).get();
    SavingGoalEntity savingGoal2Result =
        savingGoalRepository.findById(savingGoalEntity2.getId()).get();

    assertEquals(
        ChallengeState.COMPLETED, savingGoal1Result.getSavingChallenges().get(0).getState());
    assertEquals(ChallengeState.FAILED, savingGoal2Result.getSavingChallenges().get(0).getState());
  }

  @Test
  public void updateAllSavingGoalsStates() {
    SavingGoalEntity savingGoalEntity1 =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .amountSaved(300.0)
            .endingDate(LocalDate.now().plusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .title("Title")
            .userEntity(user)
            .build();

    SavingGoalEntity savingGoalEntity2 =
        SavingGoalEntity.builder()
            .state(GoalState.IN_PROGRESS)
            .goalSum(200)
            .amountSaved(100.0)
            .endingDate(LocalDate.now().minusDays(10))
            .destinationBankAccount(bankAccountEntity1)
            .sourceBankAccount(bankAccountEntity2)
            .title("Title")
            .userEntity(user)
            .build();

    savingGoalRepository.save(savingGoalEntity1);
    savingGoalRepository.save(savingGoalEntity2);

    savingGoalService.updateAllSavingGoalsStates();

    assertEquals(GoalState.COMPLETED, savingGoalEntity1.getState());
    assertEquals(GoalState.FAILED, savingGoalEntity2.getState());
  }
}
