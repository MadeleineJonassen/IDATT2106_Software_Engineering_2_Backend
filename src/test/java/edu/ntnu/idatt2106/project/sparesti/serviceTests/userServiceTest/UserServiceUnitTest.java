package edu.ntnu.idatt2106.project.sparesti.serviceTests.userServiceTest;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetails;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeTierEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.BadgeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.BankAccountService;
import edu.ntnu.idatt2106.project.sparesti.services.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserService}.
 *
 * <p>This class tests the functionality of the {@link UserService} class by mocking the responses
 * of {@link UserRepository}, and verifying the behavior of its own methods.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserServiceUnitTest {
  // Mocked dependency
  @MockBean private UserRepository userRepository;
  @MockBean private Mapper<BankAccountEntity, BankAccountDto> bankAccountMapper;

  // Service being tested
  @Autowired private UserService userService;

  @MockBean private BankAccountService bankAccountService;

  @MockBean private BadgeRepository badgeRepository;

  private UserDto userDto;

  final String email = "user@mail.com";
  final String username = "username";
  final Long id = 1L;
  final String fullName = "full name";
  final int score = 10;

  /** Sets up a UserDto before each test. */
  @BeforeEach
  void createTestUserDto() {
    // this.userDto = new UserDto(id, username, email, fullName);
    this.userDto =
        UserDto.builder()
            .id(id)
            .username(username)
            .email(email)
            .fullName(fullName)
            .score(score)
            .build();
  }

  /**
   * Verifies that {@link UserService#userExists(UserDto)} returns true when the email exists in the
   * repository.
   *
   * <p>This test mocks the {@link UserRepository} method for checking if a user exists to return
   * true for the given email.
   *
   * @see UserRepository#existsUserEntityByEmailContainingIgnoreCase(String)
   */
  @Test
  void userExistsReturnsTrueWhenEmailIsInRepository() {
    given(userRepository.existsUserEntityByEmailContainingIgnoreCase(email)).willReturn(true);
    assertTrue(userService.userExists(userDto));
  }

  /**
   * Verifies that {@link UserService#userExists(UserDto)} returns false when email is not in
   * repository.
   *
   * <p>This test mocks the {@link UserRepository} method for checking if a user exists to return
   * false for the given email.
   *
   * @see UserRepository#existsUserEntityByEmailContainingIgnoreCase(String)
   */
  @Test
  void userExistsReturnsFalseWhenEmailIsNotInRepository() {
    given(userRepository.existsUserEntityByEmailContainingIgnoreCase(email)).willReturn(false);
    assertFalse(userService.userExists(userDto));
  }

  /**
   * Verifies user creation for a non-registered email.
   *
   * <p>This test ensures that a user can be successfully created when the provided email is not
   * already registered. The {@link UserRepository} methods are mocked.
   *
   * <ul>
   *   <li>Checking if the email exists is set to return false.
   *   <li>Saving the user returns a mocked {@link UserEntity} with the same data as the UserDto
   * </ul>
   *
   * @see UserRepository#existsUserEntityByEmailContainingIgnoreCase(String)
   * @see UserRepository#save(Object)
   */
  @Test
  void createsUserWhenEmailIsNotInRepository() {
    BadgeTierEntity mockBadgeTier =
        BadgeTierEntity.builder().target(20).tier(1).description("This is a test badge").build();

    BadgeEntity mockBadge =
        BadgeEntity.builder()
            .name("Test Badge")
            .description("This is a test badge")
            .badgeTiers(Arrays.asList(mockBadgeTier))
            .build();

    UserEntity mockUserEntity =
        UserEntity.builder().id(id).email(email).fullName(fullName).username(username).build();

    // Mock repository method for checking if an email is already registered
    given(userRepository.existsUserEntityByEmailContainingIgnoreCase(userDto.getEmail()))
        .willReturn(false);
    // Mock repository method for saving a user to return a mocked version of the expected
    // UserEntity
    given(userRepository.save(any(UserEntity.class))).willReturn(mockUserEntity);

    // given(badgeRepository.findById(anyLong())).willReturn(Optional.ofNullable(mockBadge));
    given(badgeRepository.findBadgeEntityByName(anyString())).willReturn((mockBadge));

    // assert that no exceptions are thrown when creating a user
    assertDoesNotThrow(() -> userService.createUser(userDto));

    // assert that the returned userDto has expected field values
    UserDto userDtoResponse = userService.createUser(userDto);
    assertEquals(userDto.getEmail(), userDtoResponse.getEmail());
    assertEquals(userDto.getUsername(), userDtoResponse.getUsername());
    assertEquals(userDto.getFullName(), userDtoResponse.getFullName());
    assertEquals(userDto.getId(), userDtoResponse.getId());
  }

  /**
   * Verifies that an {@link ResponseStatusException} is thrown if a user with the same email
   * exists.
   *
   * <p>This test mocks the {@link UserRepository} method for checking if the provided email exists
   * to return true.
   */
  @Test
  void throwsExceptionIfUserEmailAlreadyExists() {
    given(userRepository.existsUserEntityByEmailContainingIgnoreCase(email)).willReturn(true);

    assertThrows(ResponseStatusException.class, () -> userService.createUser(userDto));
  }

  /**
   * Tests the {@link UserService#createUserWithDefaultBankAccounts(UserDto)} method to ensure it
   * correctly creates a user and assigns default bank accounts.
   *
   * <p>This test method performs several operations:
   *
   * <ul>
   *   <li>It prepares a mock {@link UserEntity} with test user data.
   *   <li>It configures the {@link UserRepository} to simulate checking if an email is already
   *       registered and to return a false, indicating the email is not registered.
   *   <li>It also configures the {@link UserRepository} to return the prepared mock {@link
   *       UserEntity} when a new user is saved.
   *   <li>The {@link BankAccountService} is mocked to simulate the assignment of default bank
   *       accounts without any actual side effects.
   * </ul>
   *
   * After setting up these mocks, the test calls the {@link
   * UserService#createUserWithDefaultBankAccounts} method with a test {@link UserDto} and checks:
   *
   * <ul>
   *   <li>That the user is created with the expected attributes.
   *   <li>That the bank account service is indeed called with the correct user ID to assign default
   *       bank accounts.
   * </ul>
   *
   * This ensures that both user creation and bank account assignment are functioning as intended
   * when an email is not previously registered.
   *
   * @see UserRepository#existsUserEntityByEmailContainingIgnoreCase(String)
   * @see UserRepository#save(Object)
   * @see BankAccountService#assignUserDefaultBankAccountSet(Long)
   */
  @Test
  void createUserWithDefaultBankAccounts() {
    Long id = 1L;
    String email = "test@example.com";
    String fullName = "Test User";
    String username = "testuser";

    BadgeTierEntity mockBadgeTier =
        BadgeTierEntity.builder().target(20).tier(1).description("This is a test badge").build();

    BadgeEntity mockBadge =
        BadgeEntity.builder()
            .name("Test Badge")
            .description("This is a test badge")
            .badgeTiers(Arrays.asList(mockBadgeTier))
            .build();

    UserEntity mockUserEntity =
        UserEntity.builder().id(id).email(email).fullName(fullName).username(username).build();
    UserDto userDto = new UserDto();
    userDto.setEmail(email);

    given(userRepository.existsUserEntityByEmailContainingIgnoreCase(userDto.getEmail()))
        .willReturn(false);

    // given(badgeRepository.findById(anyLong())).willReturn(Optional.ofNullable(mockBadge));
    given(badgeRepository.findBadgeEntityByName(anyString())).willReturn(mockBadge);

    given(userRepository.save(any(UserEntity.class))).willReturn(mockUserEntity);

    doNothing().when(bankAccountService).assignUserDefaultBankAccountSet(id);

    UserDto userDtoResponse = userService.createUserWithDefaultBankAccounts(userDto);

    assertEquals(id, userDtoResponse.getId());
    assertEquals(email, userDtoResponse.getEmail());
    assertEquals(fullName, userDtoResponse.getFullName());
    assertEquals(username, userDtoResponse.getUsername());

    verify(bankAccountService).assignUserDefaultBankAccountSet(id);
  }

  /** Tests the retrieval of all bank accounts for a specific user from the user service. */
  @Test
  void getAllBankAccounts() {
    BankAccountEntity mockBankAccountEntity1 = BankAccountEntity.builder().id(1L).build();

    BankAccountEntity mockBankAccountEntity2 = BankAccountEntity.builder().id(2L).build();

    BankAccountEntity mockBankAccountEntity3 = BankAccountEntity.builder().id(3L).build();

    List<BankAccountEntity> mockAccounts = new ArrayList<>();
    mockAccounts.add(mockBankAccountEntity1);
    mockAccounts.add(mockBankAccountEntity2);
    mockAccounts.add(mockBankAccountEntity3);

    String username = "testuser";

    UserEntity mockUserEntity =
        UserEntity.builder().username(username).bankAccounts(mockAccounts).build();

    BankAccountDto bankAccountDto = BankAccountDto.builder().build();

    when(userRepository.findUserEntityByUsername(username))
        .thenReturn(Optional.ofNullable(mockUserEntity));

    when(bankAccountMapper.mapTo(any(BankAccountEntity.class))).thenReturn(bankAccountDto);

    List<BankAccountDto> accountDtosFound = userService.getAllBankAccounts(username);

    assertEquals(3, accountDtosFound.size());
  }

  @Test
  void testGetUserDetails() {
    BankAccountEntity savingsAccount =
        BankAccountEntity.builder().sum(50).id(1L).name("Savings").build();

    BankAccountEntity checkingAccount =
        BankAccountEntity.builder().sum(31).id(2L).name("Checking").build();

    BankAccountDto savingsAccountDto =
        BankAccountDto.builder().sum(50).id(1L).name("Savings").build();

    BankAccountDto checkingAccountDto =
        BankAccountDto.builder().sum(31).id(2L).name("Checking").build();

    UserEntity userDetails =
        UserEntity.builder()
            .id(1L)
            .imageUrl("image.com")
            .email("email@email.com")
            .username("user")
            .fullName("Test User")
            .preferredSavingsAccount(savingsAccount)
            .preferredCheckingAccount(checkingAccount)
            .score(10)
            .lastCompletedChallengeDate(LocalDate.now())
            .build();

    UserDetails returnedUserDto =
        UserDetails.builder()
            .id(1L)
            .username("user")
            .fullName("Test user")
            .imageUrl("image.com")
            .preferredCheckingAccount(checkingAccountDto)
            .preferredSavingsAccount(savingsAccountDto)
            .score(10)
            .build();
    when(userRepository.findUserEntityByUsername("user"))
        .thenReturn(Optional.ofNullable(userDetails));

    // when(modelMapper.map(userDetails, UserDetails.class)).thenReturn(returnedUserDto);

    UserDetails foundDetails = userService.getUserDetails("user");
    System.out.println(foundDetails);
    assertEquals("user", foundDetails.getUsername());
    assertEquals(checkingAccountDto.getSum(), foundDetails.getPreferredCheckingAccount().getSum());
    assertEquals(savingsAccountDto.getSum(), foundDetails.getPreferredSavingsAccount().getSum());
    assertNotEquals("Not username", foundDetails.getUsername());
  }
  /*
  @Test
  void getGlobalLeaderboardReturnsSortedUserDtos() {

    UserEntity user1 =
        UserEntity.builder()
            .email("user1@mail.com")
            .fullName("User One")
            .username("userone")
            .score(10)
            .build();

    UserEntity user2 =
        UserEntity.builder()
            .email("user2@mail.com")
            .fullName("User two")
            .username("usertwo")
            .score(20)
            .build();

    List<UserEntity> users = Arrays.asList(user2, user1);

    UserDto userDto1 =
        UserDto.builder()
            .id(1L)
            .email("user1@mail.com")
            .fullName("User One")
            .username("userone")
            .score(10)
            .build();

    UserDto userDto2 =
        UserDto.builder()
            .id(2L)
            .email("user2@mail.com")
            .fullName("User two")
            .username("usertwo")
            .score(20)
            .build();

    when(userRepository.findAllByOrderByScoreDesc()).thenReturn(users);
    when(userMapper.mapTo(user1)).thenReturn(userDto1);
    when(userMapper.mapTo(user2)).thenReturn(userDto2);

    List<UserDto> result = userService.getGlobalLeaderboard();

    assertEquals(2, result.size());
    assertEquals(userDto2.getId(), result.get(0).getId());
    assertEquals(userDto1.getId(), result.get(1).getId());
  }*/
}
