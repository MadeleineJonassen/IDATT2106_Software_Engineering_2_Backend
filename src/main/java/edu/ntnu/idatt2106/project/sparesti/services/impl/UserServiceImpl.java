package edu.ntnu.idatt2106.project.sparesti.services.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetails;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetailsRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserLeaderboardDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeProgressEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.CompletedBadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.BadgeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.BankAccountRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.BankAccountService;
import edu.ntnu.idatt2106.project.sparesti.services.UserService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of the UserService interface.
 *
 * <p>This class provides methods to interact with user data, including creating users and checking
 * if a user exists. Dependency {@link UserRepository}: to interact with the database. Dependency
 * {@link Mapper}: to map between {@link UserDto} and {@link UserEntity} objects.
 */
@Log
@Service
public class UserServiceImpl implements UserService {

  /** Used for Dependency Injection. */
  private UserRepository userRepository;

  private ModelMapper modelMapper;

  private BankAccountRepository bankAccountRepository;

  private BadgeRepository badgeRepository;

  /** Used for Dependency Injection. */
  private Mapper<UserEntity, UserDto> userMapper;

  private BankAccountService bankAccountService;
  private Mapper<BankAccountEntity, BankAccountDto> bankAccountMapper;

  /**
   * Constructor for a new UserServiceImpl with the provided dependencies.
   *
   * @param userRepository the UserRepository instance to interact with user data in the database.
   * @param userMapper the Mapper instance to map between UserEntity and UserDto objects.
   */
  public UserServiceImpl(
      UserRepository userRepository,
      Mapper<UserEntity, UserDto> userMapper,
      BankAccountService bankAccountService,
      Mapper<BankAccountEntity, BankAccountDto> bankAccountMapper,
      ModelMapper modelMapper,
      BankAccountRepository bankAccountRepository,
      BadgeRepository badgeRepository) {
    this.userMapper = userMapper;
    this.userRepository = userRepository;
    this.bankAccountService = bankAccountService;
    this.bankAccountMapper = bankAccountMapper;
    this.modelMapper = modelMapper;
    this.bankAccountRepository = bankAccountRepository;
    this.badgeRepository = badgeRepository;
  }

  /**
   * Creates a new user based on the provided UserDto.
   *
   * @param userDto the UserDto object containing the user data to be created.
   * @return the newly created UserDto.
   * @see UserRepository#save(Object)
   * @see #userExists(UserDto)
   */
  @Override
  public UserDto createUser(UserDto userDto) {
    if (userExists(userDto)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists in database.");
    }
    UserEntity userEntity =
        UserEntity.builder()
            .email(userDto.getEmail())
            .username(userDto.getUsername())
            .fullName(userDto.getFullName())
            .imageUrl(userDto.getImageUrl())
            .score(0)
            .completedChallenges(0)
            .completedGoals(0)
            .currentStreak(0)
            .totalSaved(0.0)
            .subId(userDto.getSubId())
            .build();

    BadgeEntity badgeEntity = badgeRepository.findBadgeEntityByName("Griseglad");

    CompletedBadgeEntity completedBadge =
        CompletedBadgeEntity.builder()
            .name("Griseglad")
            .description(badgeEntity.getDescription())
            .build();

    BadgeProgressEntity badgeProgress =
        BadgeProgressEntity.builder().badgeEntity(badgeEntity).userEntity(userEntity).build();

    userEntity.addBadgeProgress(badgeProgress);
    userEntity.addCompletedBadge(completedBadge);

    UserEntity savedUser = userRepository.save(userEntity);
    return userMapper.mapTo(savedUser);
  }

  /**
   * Checks if a user with the same email as the provided UserDto exists in the database.
   *
   * @param userDto the UserDto object containing the user data to be checked.
   * @return true if a user with the same email exists, false otherwise.
   */
  @Override
  public Boolean userExists(UserDto userDto) {
    UserEntity userToCheck = userMapper.mapFrom(userDto);
    return userRepository.existsUserEntityByEmailContainingIgnoreCase(userToCheck.getEmail());
  }

  /**
   * Calls {@link BankAccountService} to assign a user some default account for testing purposes.
   *
   * @param userDto The UserDto object containing the data of the user.
   * @return A UserDto containing the data of the user.
   */
  public UserDto createUserWithDefaultBankAccounts(UserDto userDto) {
    UserDto createdUser = createUser(userDto);

    bankAccountService.assignUserDefaultBankAccountSet(createdUser.getId());

    return createdUser;
  }

  /**
   * Retrieves all bank accounts associated with a given username.
   *
   * @param username the username whose bank accounts are to be retrieved; must not be null or
   *     empty.
   * @return a list of BankAccountDto representing the bank accounts associated with the provided
   *     username. Returns an empty list if the user exists but has no bank accounts.
   */
  public List<BankAccountDto> getAllBankAccounts(String username) {
    List<BankAccountEntity> accountsInUser =
        userRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user with that username."))
            .getBankAccounts();

    return accountsInUser.stream()
        .map(bankAccountEntity -> bankAccountMapper.mapTo(bankAccountEntity))
        .toList();
  }

  /**
   * Takes in a username and provides detailed information about that user.
   *
   * @param username Username to fetch for
   * @return Detailed user information
   */
  @Override
  public UserDetails getUserDetails(String username) {
    UserEntity userEntity =
        userRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user with that username."));
    if (userEntity.getLastCompletedChallengeDate() != null) {
      if (ChronoUnit.DAYS.between(userEntity.getLastCompletedChallengeDate(), LocalDate.now())
          > 7) {
        userEntity.resetStreak();
        userRepository.save(userEntity);
      }
    }
    return modelMapper.map(userEntity, UserDetails.class);
  }

  /**
   * Edits user information.
   *
   * @param username Username of the user to modify
   * @param userDetailsRequestDto Updated user detail fields
   * @return Updated user details
   */
  @Override
  public UserDetails editUser(String username, UserDetailsRequestDto userDetailsRequestDto) {
    UserEntity currentUser =
        userRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user with that username"));
    if (userDetailsRequestDto.getPreferredCheckingAccountId() != null) {
      BankAccountEntity newCheckingAccount =
          bankAccountRepository
              .findById(userDetailsRequestDto.getPreferredCheckingAccountId())
              .orElseThrow(
                  () ->
                      new ResponseStatusException(
                          HttpStatus.NOT_FOUND, "No account with that id."));
      if (currentUser.getBankAccounts().stream()
          .anyMatch(
              bankAccountEntity ->
                  bankAccountEntity
                      .getId()
                      .equals(userDetailsRequestDto.getPreferredCheckingAccountId()))) {
        currentUser.setPreferredCheckingAccount(newCheckingAccount);
      } else {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "You do not have access to that account.");
      }
    }

    if (userDetailsRequestDto.getPreferredSavingsAccountId() != null) {
      BankAccountEntity newSavingAccount =
          bankAccountRepository
              .findById(userDetailsRequestDto.getPreferredSavingsAccountId())
              .orElseThrow(
                  () ->
                      new ResponseStatusException(
                          HttpStatus.NOT_FOUND, "No account with that id."));
      if (currentUser.getBankAccounts().stream()
          .anyMatch(
              bankAccountEntity ->
                  bankAccountEntity
                      .getId()
                      .equals(userDetailsRequestDto.getPreferredSavingsAccountId()))) {
        currentUser.setPreferredSavingsAccount(newSavingAccount);
      } else {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "You do not have access to that account.");
      }
    }

    if (userDetailsRequestDto.getImageUrl() != null) {
      currentUser.setImageUrl(userDetailsRequestDto.getImageUrl());
    }
    return modelMapper.map(userRepository.save(currentUser), UserDetails.class);
  }

  /**
   * Method for returning the global leaderboard of users. This is ordered by user points.
   *
   * @return a list of every user in a point-descending order
   */
  @Override
  public List<UserLeaderboardDto> getGlobalLeaderboard() {
    // Fetch all UserEntity objects ordered by score in descending order
    List<UserEntity> users = userRepository.findAllByOrderByScoreDesc();

    // Use userMapper to transform each UserEntity into a UserDto
    return users.stream()
        .map(userEntity -> modelMapper.map(userEntity, UserLeaderboardDto.class))
        .toList();
    // return users.stream().map(user -> userMapper.mapTo(user)).collect(Collectors.toList());
  }

  /**
   * Retrieves the subId for a user given their username. The subId is a unique identifier for the
   * user which is generally used for linking the user details in a JWT (JSON Web Token)
   * authentication flow.
   *
   * @param username The username of the user for whom the subId is to be retrieved.
   * @return The subId associated with the given username.
   * @throws ResponseStatusException if no user is found with the provided username. The HTTP status
   *     code thrown is HttpStatus.NOT_FOUND.
   */
  @Override
  public String getUserFromSubId(String username) {
    UserEntity userEntity =
        userRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user with that username."));

    return userEntity.getSubId();
  }
}
