package edu.ntnu.idatt2106.project.sparesti.serviceTests.savingGoalServiceTests;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoDetailsResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoGeneralResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoPost;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.*;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.BadgeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.BankAccountRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.SavingGoalRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.impl.SavingGoalServiceImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SavingGoalServiceImpl} class to verify the correct behavior of saving goal
 * operations.
 */
@SpringBootTest
@ActiveProfiles("test")
public class SavingGoalServiceUnitTest {

  @Mock private SavingGoalRepository savingGoalRepository;

  @Mock private UserRepository userRepository;

  @Mock private BankAccountRepository bankAccountRepository;

  @Mock private Mapper<SavingGoalEntity, SavingGoalDtoGeneralResponse> savingGoalMapper;

  @Mock private BadgeRepository badgeRepository;

  @InjectMocks private SavingGoalServiceImpl savingGoalService;

  private BankAccountEntity bankAccountEntity1;
  private BankAccountEntity bankAccountEntity2;
  private SavingGoalEntity savingGoalEntity;
  private UserEntity userEntity;

  /**
   * Initializes mocks and sets up common objects and scenarios used across multiple tests. This
   * setup includes initializing the user entity with bank accounts and setting up the necessary
   * saving goal entity links between source and destination accounts.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    bankAccountEntity1 = BankAccountEntity.builder().id(1L).build();
    bankAccountEntity2 = BankAccountEntity.builder().id(2L).build();

    savingGoalEntity =
        SavingGoalEntity.builder()
            .sourceBankAccount(bankAccountEntity1)
            .destinationBankAccount(bankAccountEntity2)
            .state(GoalState.IN_PROGRESS)
            .goalSum(100)
            .endingDate(LocalDate.of(2022, 1, 1))
            .build();

    userEntity = new UserEntity();
    userEntity.setUsername("john_doe");
    userEntity.setCompletedGoals(1);
    userEntity.setCompletedChallenges(1);
    userEntity.setCurrentStreak(1);

    List<BankAccountEntity> bankAccountEntityList = new ArrayList<>();
    bankAccountEntityList.add(bankAccountEntity1);
    bankAccountEntityList.add(bankAccountEntity2);
    userEntity.setBankAccounts(bankAccountEntityList);
  }

  /**
   * Tests the successful creation of a saving goal. This test verifies if the saving goal service
   * correctly creates a new saving goal when all required inputs are provided. It checks the
   * interactions with repositories and mapper to ensure a saving goal is saved and mapped
   * correctly.
   */
  @Test
  public void testCreateSavingGoalSuccessful() {
    LocalDate date = LocalDate.of(2022, 1, 1);
    SavingGoalDtoPost dto = new SavingGoalDtoPost();
    dto.setTitle("New Car");
    dto.setEndingDate(date);
    dto.setGoalSum(10000);
    dto.setSourceBankAccountId(1L);
    dto.setDestinationBankAccountId(2L);

    when(userRepository.findUserEntityByUsername("john_doe")).thenReturn(Optional.of(userEntity));
    when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankAccountEntity1));
    when(savingGoalRepository.save(any(SavingGoalEntity.class))).thenReturn(savingGoalEntity);
    when(savingGoalMapper.mapTo(any(SavingGoalEntity.class)))
        .thenReturn(new SavingGoalDtoGeneralResponse());

    SavingGoalDtoGeneralResponse response = savingGoalService.createSavingGoal(dto, "john_doe");

    assertNotNull(response);
    verify(savingGoalRepository, times(1)).save(any(SavingGoalEntity.class));
  }

  /**
   * Tests the saving goal creation with missing mandatory fields in the input DTO. This test
   * expects the service to throw a ResponseStatusException due to incomplete data.
   */
  @Test
  public void testCreateSavingGoalWithMissingFields() {
    SavingGoalDtoPost dto = new SavingGoalDtoPost();

    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> savingGoalService.createSavingGoal(dto, "john_doe"));
    assertTrue(exception.getMessage().contains("Missing fields"));
  }

  /**
   * Tests the unauthorized modification of a saving goal. This test ensures that an unauthorized
   * user cannot edit a saving goal and expects the service to throw a ResponseStatusException when
   * such an attempt is made.
   */
  @Test
  public void testEditSavingGoalUnauthorizedUser() {
    SavingGoalDtoPost dto = new SavingGoalDtoPost();
    dto.setSourceBankAccountId(1L);
    dto.setDestinationBankAccountId(2L);

    when(userRepository.findUserEntityByUsername("john_doe")).thenReturn(Optional.of(userEntity));
    when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankAccountEntity1));
    when(savingGoalRepository.save(any(SavingGoalEntity.class))).thenReturn(savingGoalEntity);
    when(savingGoalMapper.mapTo(any(SavingGoalEntity.class)))
        .thenReturn(new SavingGoalDtoGeneralResponse());
    when(savingGoalRepository.findById(anyLong())).thenReturn(Optional.of(new SavingGoalEntity()));

    Exception exception =
        assertThrows(
            ResponseStatusException.class,
            () -> savingGoalService.editSavingGoal(dto, "john_doe", 1L));
    assertTrue(exception.getMessage().contains("You are not allowed to modify this goal"));
  }

  /**
   * Tests retrieval of all saving goals associated with a specific user. This test validates if the
   * service properly fetches and maps all saving goals for a user.
   */
  @Test
  public void testGetAllSavingGoalsForUser() {
    List<SavingGoalEntity> savingGoalEntities = new ArrayList<>();
    savingGoalEntities.add(savingGoalEntity);
    userEntity.setSavingGoals(savingGoalEntities);

    when(userRepository.findUserEntityByUsername("john_doe")).thenReturn(Optional.of(userEntity));
    when(savingGoalMapper.mapTo(any())).thenReturn(new SavingGoalDtoGeneralResponse());

    var response = savingGoalService.getAllSavingGoalsForUser("john_doe");

    assertNotNull(response);
    verify(savingGoalMapper, times(1)).mapTo(any());
  }

  /**
   * Tests the behavior of the saving goal service when attempting to retrieve a non-existent saving
   * goal. This test ensures that the service correctly handles the scenario where a saving goal id
   * does not exist, resulting in a ResponseStatusException with a 404 status.
   */
  @Test
  public void testGetSavingGoalNotFound() {
    when(savingGoalRepository.findById(anyLong())).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(ResponseStatusException.class, () -> savingGoalService.getSavingGoal(1L));
    assertEquals("404 NOT_FOUND \"No goal with that id.\"", exception.getMessage());
  }

  /**
   * Tests the state update of a saving goal on retrieval. This test checks if the state of a saving
   * goal is correctly updated to COMPLETED when the conditions (goal sum met and past due date) are
   * satisfied. Verifies that the update is persisted by checking interactions with the repository.
   */
  @Test
  public void testStateUpdateOnGoalRetrieval() {
    Long goalId = 1L;
    SavingGoalEntity goal =
        SavingGoalEntity.builder()
            .goalSum(1000)
            .amountSaved(1000)
            .endingDate(LocalDate.now().minusDays(1))
            .state(GoalState.IN_PROGRESS)
            .userEntity(userEntity)
            .build();

    BadgeEntity badgeEntity = BadgeEntity.builder().name("Supersparings-gris").id(1L).build();

    BadgeTierEntity badgeTierEntity =
        BadgeTierEntity.builder().tier(1).badge(badgeEntity).target(5).build();

    BadgeProgressEntity badgeProgressEntity =
        BadgeProgressEntity.builder()
            .userEntity(userEntity)
            .badgeEntity(badgeEntity)
            .progress(1)
            .build();

    badgeEntity.setBadgeTiers(Arrays.asList(badgeTierEntity));
    badgeRepository.save(badgeEntity);
    userEntity.addBadgeProgress(badgeProgressEntity);

    when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(goal));
    when(savingGoalRepository.save(any(SavingGoalEntity.class))).thenReturn(goal);
    when(badgeRepository.findBadgeEntityByName(anyString())).thenReturn(badgeEntity);

    SavingGoalDtoDetailsResponse result = savingGoalService.getSavingGoal(goalId);

    assertEquals(GoalState.COMPLETED, result.getState());
    verify(savingGoalRepository, times(1)).save(goal);
  }

  /**
   * Tests the state update of a saving goal on editing. This test verifies that the state of a
   * saving goal is correctly updated to COMPLETED when modifications to the goal satisfy the
   * conditions for completion.
   */
  @Test
  public void testStateUpdateOnGoalEdit() {
    Long goalId = 1L;
    SavingGoalDtoPost dto = new SavingGoalDtoPost();
    dto.setGoalSum(900);
    dto.setEndingDate(LocalDate.now());

    SavingGoalEntity existingGoal =
        SavingGoalEntity.builder()
            .goalSum(1500)
            .amountSaved(1000)
            .endingDate(LocalDate.now().plusDays(1))
            .state(GoalState.IN_PROGRESS)
            .sourceBankAccount(bankAccountEntity1)
            .destinationBankAccount(bankAccountEntity2)
            .userEntity(userEntity)
            .build();

    BadgeEntity badgeEntity = BadgeEntity.builder().name("Supersparings-gris").id(1L).build();

    BadgeTierEntity badgeTierEntity =
        BadgeTierEntity.builder().tier(1).target(5).badge(badgeEntity).build();
    badgeEntity.setBadgeTiers(Arrays.asList(badgeTierEntity));
    badgeRepository.save(badgeEntity);
    BadgeProgressEntity badgeProgressEntity =
        BadgeProgressEntity.builder()
            .badgeEntity(badgeEntity)
            .progress(1)
            .userEntity(userEntity)
            .build();

    userEntity.addBadgeProgress(badgeProgressEntity);

    userEntity.setSavingGoals(List.of(existingGoal));

    when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(userEntity));
    when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
    when(savingGoalRepository.save(any(SavingGoalEntity.class))).thenReturn(existingGoal);
    when(badgeRepository.findBadgeEntityByName(anyString())).thenReturn(badgeEntity);

    savingGoalService.editSavingGoal(dto, "john_doe", goalId);

    assertEquals(GoalState.COMPLETED, existingGoal.getState());
    verify(savingGoalRepository, times(1)).save(existingGoal);
  }

  /**
   * Tests that there is no state change for a saving goal during an edit operation when the
   * conditions for state change are not met.
   */
  @Test
  public void testNoStateChangeIfConditionsNotMetOnEdit() {
    Long goalId = 1L;
    SavingGoalDtoPost dto = new SavingGoalDtoPost();
    dto.setGoalSum(1500);
    dto.setEndingDate(LocalDate.now().plusDays(5));

    SavingGoalEntity existingGoal =
        SavingGoalEntity.builder()
            .goalSum(2000)
            .amountSaved(1000)
            .endingDate(LocalDate.now().plusDays(10))
            .state(GoalState.IN_PROGRESS)
            .sourceBankAccount(bankAccountEntity1)
            .destinationBankAccount(bankAccountEntity2)
            .build();

    userEntity.setSavingGoals(List.of(existingGoal));

    when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(userEntity));
    when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
    when(savingGoalRepository.save(any(SavingGoalEntity.class))).thenReturn(existingGoal);

    savingGoalService.editSavingGoal(dto, "john_doe", goalId);

    assertEquals(GoalState.IN_PROGRESS, existingGoal.getState());
  }

  /**
   * Tests the retrieval of all saving goals for a user with a focus on state transition to FAILED.
   * This test ensures that when retrieving saving goals, those that are overdue and underfunded are
   * automatically transitioned to FAILED state. It also checks if the changes are correctly saved
   * in the repository.
   */
  @Test
  public void testGetAllSavingGoalsStateTransitionToFailed() {
    String username = "john_doe";
    Long goalId = 1L;
    LocalDate pastDueDate = LocalDate.now().minusDays(10);
    SavingGoalEntity overdueGoal =
        SavingGoalEntity.builder()
            .id(goalId)
            .goalSum(10000)
            .amountSaved(5000)
            .endingDate(pastDueDate)
            .state(GoalState.IN_PROGRESS)
            .build();

    UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setSavingGoals(Arrays.asList(overdueGoal));

    when(userRepository.findUserEntityByUsername(username)).thenReturn(Optional.of(user));
    when(savingGoalRepository.save(any(SavingGoalEntity.class))).thenReturn(overdueGoal);
    when(savingGoalMapper.mapTo(any(SavingGoalEntity.class)))
        .thenReturn(new SavingGoalDtoGeneralResponse());

    List<SavingGoalDtoGeneralResponse> results =
        savingGoalService.getAllSavingGoalsForUser(username);

    assertNotNull(results);
    assertFalse(results.isEmpty());
    assertEquals(1, results.size());
    verify(savingGoalRepository, times(1)).save(overdueGoal);
    assertEquals(GoalState.FAILED, overdueGoal.getState());
  }
}
