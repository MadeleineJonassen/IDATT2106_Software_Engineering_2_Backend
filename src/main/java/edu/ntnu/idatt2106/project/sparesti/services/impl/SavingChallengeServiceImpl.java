package edu.ntnu.idatt2106.project.sparesti.services.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransferDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeResponseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeProgressEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeTierEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.CompletedBadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingChallengeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingGoalEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.ChallengeState;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.BadgeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.SavingChallengeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.SavingGoalRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionCategoryRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.BankAccountService;
import edu.ntnu.idatt2106.project.sparesti.services.SavingChallengeService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of the SavingChallengeService interface, handling the business logic associated
 * with saving challenges.
 */
@Log
@Service
public class SavingChallengeServiceImpl implements SavingChallengeService {
  private SavingChallengeRepository savingChallengeRepository;

  private SavingGoalRepository savingGoalRepository;

  private UserRepository userRepository;
  private TransactionCategoryRepository transactionCategoryRepository;

  private Mapper<SavingChallengeEntity, SavingChallengeResponseDto> savingChallengeMapper;

  private BankAccountService bankAccountService;

  private BadgeRepository badgeRepository;

  /**
   * Constructor for the SavingChallengeServiceImpl with necessary dependencies.
   *
   * @param savingChallengeRepository Repository for accessing saving challenges.
   * @param savingGoalRepository Repository for accessing saving goals.
   * @param transactionCategoryRepository Repository for accessing transaction categories.
   * @param savingChallengeMapper Mapper for converting between entity and DTO for saving
   *     challenges.
   * @param userRepository Repository for accessing user data.
   * @param bankAccountService Service for accessing bank account information.
   * @param badgeRepository Repository for accessing badges
   */
  public SavingChallengeServiceImpl(
      SavingChallengeRepository savingChallengeRepository,
      SavingGoalRepository savingGoalRepository,
      TransactionCategoryRepository transactionCategoryRepository,
      Mapper<SavingChallengeEntity, SavingChallengeResponseDto> savingChallengeMapper,
      UserRepository userRepository,
      BankAccountService bankAccountService,
      BadgeRepository badgeRepository) {
    this.savingChallengeRepository = savingChallengeRepository;
    this.savingGoalRepository = savingGoalRepository;
    this.transactionCategoryRepository = transactionCategoryRepository;
    this.savingChallengeMapper = savingChallengeMapper;
    this.userRepository = userRepository;
    this.bankAccountService = bankAccountService;
    this.badgeRepository = badgeRepository;
  }

  /**
   * Creates a new saving challenge as part of a specified savings goal.
   *
   * @param savingChallengeDto The DTO containing the data needed to create a saving challenge.
   * @param goalId The ID of the goal under which the challenge will be created.
   * @return The newly created saving challenge as a DTO.
   * @throws ResponseStatusException If the specified goal or category does not exist.
   */
  @Override
  public SavingChallengeResponseDto createSavingChallenge(
      SavingChallengeRequestDto savingChallengeDto, Long goalId) {
    if (savingChallengeDto.getCategoryId() == null
        || savingChallengeDto.getSpendingGoal() == null
        || savingChallengeDto.getSpendingGoal() <= 0
        || savingChallengeDto.getExpectedExpense() == null
        || savingChallengeDto.getExpectedExpense() <= 0
        || savingChallengeDto.getEndingDate() == null
        || savingChallengeDto.getStartDate() == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Missing or bad fields in request.");
    }

    SavingGoalEntity savingGoalEntity =
        savingGoalRepository
            .findById(goalId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No goal with that id."));

    UserEntity userEntity = savingGoalEntity.getUserEntity();

    if (!savingGoalEntity.getState().equals(GoalState.IN_PROGRESS)) {
      log.info("Cannot add challenge to saving goal not in progress.");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Cannot add challenge to saving goal not in progress.");
    }

    List<SavingChallengeEntity> allSavingChallengeEntities =
        userEntity.getSavingGoals().stream()
            .flatMap(goal -> goal.getSavingChallenges().stream())
            .collect(Collectors.toList());

    if (allSavingChallengeEntities.stream()
        .anyMatch(
            savingChallengeEntity ->
                savingChallengeEntity
                        .getTransactionCategory()
                        .getId()
                        .equals(savingChallengeDto.getCategoryId())
                    && savingChallengeEntity.getState().equals(ChallengeState.IN_PROGRESS))) {
      log.info("Saving challenge with that category already exists.");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Saving challenge with the category already exists.");
    }

    TransactionCategoryEntity transactionCategoryEntity =
        transactionCategoryRepository
            .findById(savingChallengeDto.getCategoryId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "No category with that id."));

    SavingChallengeEntity savingChallengeEntity =
        SavingChallengeEntity.builder()
            .savingGoal(savingGoalEntity)
            .transactionCategory(transactionCategoryEntity)
            .expectedExpense(savingChallengeDto.getExpectedExpense())
            .endingDate(savingChallengeDto.getEndingDate())
            .startDate(savingChallengeDto.getStartDate())
            .spendingGoal(savingChallengeDto.getSpendingGoal())
            .state(ChallengeState.IN_PROGRESS)
            .build();

    SavingChallengeEntity savedChallenge = savingChallengeRepository.save(savingChallengeEntity);

    SavingChallengeResponseDto responseDto = savingChallengeMapper.mapTo(savedChallenge);
    responseDto.setCurrentSpending(0);
    return responseDto;
  }

  /**
   * Service that gets all saving challenges tied to a goal. Also calls the
   * findCurrentSpendingOnChallenge method to track the progress on the challenges.
   *
   * @param goalId the chosen goal to get all challenges to.
   * @return a list og saving challenge Dto's.
   * @throws ResponseStatusException If the user does not exist.
   */
  @Override
  public List<SavingChallengeResponseDto> getAndProcessAllChallengesInGoal(Long goalId) {
    SavingGoalEntity savingGoalEntity =
        savingGoalRepository
            .findById(goalId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "That goal does not exist"));
    return processChallenges(savingGoalEntity.getSavingChallenges());
  }

  /**
   * Service that gets all saving goals tied to a user, then every challenge tied to each goal. Also
   * calls the findCurrentSpendingOnChallenge method to track the progress on the challenges.
   *
   * @param username the chosen user to get all challenges to.
   * @return a list og saving challenge Dto's.
   * @throws ResponseStatusException If the user does not exist.
   */
  @Override
  public List<SavingChallengeResponseDto> getAndProcessUserChallenges(String username) {
    UserEntity userEntity =
        userRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "That user does not exist"));

    List<SavingChallengeEntity> allSavingChallengeEntities =
        userEntity.getSavingGoals().stream()
            .flatMap(goal -> goal.getSavingChallenges().stream())
            .collect(Collectors.toList());

    return processChallenges(allSavingChallengeEntities);
  }

  /**
   * Completes a saving challenge, transferring the saved amount, and updating the challenge and
   * goal states.
   *
   * @param savingChallengeId The ID of the saving challenge to complete.
   * @return The updated saving challenge as a DTO.
   * @throws ResponseStatusException If the challenge is not completed or if the transfer fails due
   *     to insufficient funds.
   */
  @Transactional
  public SavingChallengeResponseDto finishChallenge(Long savingChallengeId, String username) {
    SavingChallengeEntity savingChallenge = fetchChallenge(savingChallengeId);

    if (!savingChallenge.getState().equals(ChallengeState.COMPLETED)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Challenge is not completed.");
    }

    SavingChallengeResponseDto responseDto = mapSavingChallengeEntityToResponseDto(savingChallenge);

    if (checkIfSpendingChallengeFailed(responseDto.getCurrentSpending(), savingChallenge)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "That challenge has been failed, so could not transfer savings.");
    }

    transferSavings(responseDto, savingChallenge);

    savingChallenge.setState(ChallengeState.COMPLETED_AND_TRANSFERRED);
    responseDto.setState(ChallengeState.COMPLETED_AND_TRANSFERRED);

    UserEntity userEntity = savingChallenge.getSavingGoal().getUserEntity();
    BadgeEntity badgeEntity1 = badgeRepository.findBadgeEntityByName("Teenage Mutant Ninja-piggy");
    BadgeEntity badgeEntity2 = badgeRepository.findBadgeEntityByName("Griseflink sparer");

    if (userEntity.getBadgeProgressEntityByName("Griseflink sparer") == null) {
      BadgeProgressEntity badgeProgressEntity =
          BadgeProgressEntity.builder().userEntity(userEntity).badgeEntity(badgeEntity2).build();

      CompletedBadgeEntity completedBadge =
          CompletedBadgeEntity.builder()
              .name(badgeEntity2.getName())
              .description(badgeEntity2.getDescription())
              .build();
      userEntity.addCompletedBadge(completedBadge);
      userEntity.addBadgeProgress(badgeProgressEntity);
    }

    if (userEntity.getBadgeProgressEntityByName("Teenage Mutant Ninja-piggy") == null) {
      BadgeProgressEntity badgeProgress =
          BadgeProgressEntity.builder()
              .badgeEntity(badgeEntity1)
              .userEntity(userEntity)
              .progress(1)
              .build();
      userEntity.addBadgeProgress(badgeProgress);
      userEntity.increaseCompletedChallenges();
    } else {
      userEntity.getBadgeProgressEntityByName("Teenage Mutant Ninja-piggy").increaseProgress();
      userEntity.increaseCompletedChallenges();
    }
    if (userEntity.getLastCompletedChallengeDate() == null
        || ChronoUnit.DAYS.between(userEntity.getLastCompletedChallengeDate(), LocalDate.now())
            > 7) {
      userEntity.setCurrentStreak(1);
      userEntity.setLastStreakDate(LocalDate.now());
    } else if (ChronoUnit.DAYS.between(userEntity.getLastStreakDate(), LocalDate.now()) >= 7
        && ChronoUnit.DAYS.between(userEntity.getLastCompletedChallengeDate(), LocalDate.now())
            <= 7) {
      userEntity.increaseStreak();
      userEntity.setLastStreakDate(LocalDate.now());
    }
    userEntity.setLastCompletedChallengeDate(LocalDate.now());

    for (BadgeTierEntity badgeTier : badgeEntity1.getBadgeTiers()) {
      CompletedBadgeEntity completedBadge =
          CompletedBadgeEntity.builder()
              .name(badgeEntity1.getName())
              .tier(badgeTier.getTier())
              .description(badgeTier.getDescription())
              .build();
      if (userEntity.getBadgeProgressEntityByName("Teenage Mutant Ninja-piggy").getProgress()
              >= badgeTier.getTarget()
          && !userEntity.getCompletedBadges().stream()
              .anyMatch(
                  completed ->
                      completed.getName().equals(badgeEntity1.getName())
                          && completed.getTier().equals(badgeTier.getTier()))) {
        userEntity.addCompletedBadge(completedBadge);
      }
    }
    savingChallenge.setAmountSaved(responseDto.getAmountSaved());

    savingChallengeRepository.save(savingChallenge);

    UserEntity user =
        userRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "That user does not exist"));

    int newUserScore = getScoreFromSavingChallenge(savingChallenge) + user.getScore();
    user.setScore(newUserScore);
    user.increaseTotalSaved(savingChallenge.getAmountSaved());
    userRepository.save(user);

    updateSavingGoalAfterChallengeTransfer(savingChallenge);

    return responseDto;
  }

  private int getScoreFromSavingChallenge(SavingChallengeEntity completedSavingChallenge) {
    // TODO: Add streaks and tweak algorithm
    int amountSaved = completedSavingChallenge.getAmountSaved().intValue();
    return amountSaved / 10;
  }

  @Override
  public void updateAllSavingChallengeStates() {
    savingChallengeRepository.findAll().stream()
        .filter(savingChallenge -> savingChallenge.getState().equals(ChallengeState.IN_PROGRESS))
        .forEach(
            savingChallenge -> {
              if (updateChallengeState(
                  findCurrentSpendingOnChallenge(savingChallenge), savingChallenge)) {
                savingChallengeRepository.save(savingChallenge);
              }
            });
  }

  @Override
  public List<SavingChallengeResponseDto> getChallengeSuggestions(Long goalId) {
    List<SavingChallengeResponseDto> savingChallengeSuggestions = new ArrayList<>();
    SavingGoalEntity savingGoal =
        savingGoalRepository
            .findById(goalId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saving goal not found."));

    BankAccountEntity sourceAccount = savingGoal.getSourceBankAccount();

    List<TransactionCategoryEntity> validCategories =
        findAvailableCategoriesForUser(savingGoal.getUserEntity());

    for (TransactionCategoryEntity category : validCategories) {
      Optional<SavingChallengeResponseDto> generatedChallenge =
          generateChallengeSuggestionForCategory(sourceAccount.getId(), category);
      generatedChallenge.ifPresent(savingChallengeSuggestions::add);
    }

    sortChallengesBySpendingDifference(savingChallengeSuggestions);

    return savingChallengeSuggestions;
  }

  /**
   * Generates a saving challenge suggestion for a specific bank account and category. The
   * suggestion is based on the average expenditure on that category over the past week and
   * calculated spending goal, which is determined from the difference between projected expenditure
   * and suggested amount.
   *
   * @param bankAccountId The ID of the bank account associated with the saving goal.
   * @param category The transaction category for which to generate a challenge suggestion.
   * @return An {@link Optional} containing a {@link SavingChallengeResponseDto} if a valid
   *     challenge can be created, or an empty Optional otherwise.
   */
  private Optional<SavingChallengeResponseDto> generateChallengeSuggestionForCategory(
      Long bankAccountId, TransactionCategoryEntity category) {
    double projectedWeeklyExpenditure =
        bankAccountService.getAverageExpenditureOnCategory(bankAccountId, category.getId(), 7);
    Optional<Integer> savingAmountOptional =
        generateSavingAmount(projectedWeeklyExpenditure, category);
    int expectedExpense = (int) Math.round(projectedWeeklyExpenditure);

    return savingAmountOptional.flatMap(
        savingAmount -> {
          SavingChallengeResponseDto dto =
              SavingChallengeResponseDto.builder()
                  .startDate(LocalDate.now())
                  .endingDate(LocalDate.now().plusDays(7))
                  .spendingGoal(expectedExpense - savingAmount)
                  .expectedExpense(expectedExpense)
                  .transactionCategory(
                      TransactionCategoryDto.builder()
                          .id(category.getId())
                          .name(category.getName())
                          .build())
                  .build();

          return Optional.of(dto);
        });
  }

  /**
   * Calculates a suggested amount to save based on the difference between the projected weekly
   * expenditure and the suggested amount for a category. A positive difference results in a
   * suggested goal; otherwise, no goal is suggested.
   *
   * @param projectedWeeklyExpenditure The projected weekly expenditure for the category.
   * @param category The transaction category for which to determine a spending goal.
   * @return An {@link Optional} containing the calculated spending goal if there is a positive
   *     difference, or an empty Optional if not.
   */
  private Optional<Integer> generateSavingAmount(
      double projectedWeeklyExpenditure, TransactionCategoryEntity category) {
    double expenditureDiff = projectedWeeklyExpenditure - category.getSuggestedAmount();

    if (expenditureDiff > 0) {
      return Optional.of((int) Math.round(expenditureDiff / 2));
    }
    return Optional.empty();
  }

  /**
   * Retrieves all saving challenges in progress associated with all saving goals for a given user.
   *
   * @param userEntity The user entity whose saving challenges are to be retrieved.
   * @return A list of {@link SavingChallengeEntity} objects representing all challenges associated
   *     with the user.
   */
  private List<SavingChallengeEntity> getAllChallengesInProgressInUser(UserEntity userEntity) {
    return userEntity.getSavingGoals().stream()
        .flatMap(goal -> goal.getSavingChallenges().stream())
        .filter(challenge -> challenge.getState() == ChallengeState.IN_PROGRESS)
        .collect(Collectors.toList());
  }

  /**
   * Identifies and returns a list of transaction categories associated with a user that currently
   * have no active saving challenges.
   *
   * @param user The user entity to check for available transaction categories.
   * @return A list of {@link TransactionCategoryEntity} objects representing categories with no
   *     active challenges.
   */
  private List<TransactionCategoryEntity> findAvailableCategoriesForUser(UserEntity user) {
    List<SavingChallengeEntity> savingChallengesInUser = getAllChallengesInProgressInUser(user);
    List<Long> uniqueTransactionCategoryIds =
        savingChallengesInUser.stream()
            .map(SavingChallengeEntity::getTransactionCategory)
            .distinct()
            .map(TransactionCategoryEntity::getId)
            .toList();
    if (uniqueTransactionCategoryIds.isEmpty()) {
      return transactionCategoryRepository.findAll();
    } else {
      return transactionCategoryRepository.findByIdNotIn(uniqueTransactionCategoryIds);
    }
  }

  /**
   * Sorts a list of Saving challenge Response DTOs by the difference between the expected expense
   * and spending goal in descending order.
   *
   * @param challenges The challenges which are sorted.
   */
  private void sortChallengesBySpendingDifference(List<SavingChallengeResponseDto> challenges) {
    challenges.sort(
        (dto1, dto2) -> {
          double diff1 = Math.abs(dto1.getExpectedExpense() - dto1.getSpendingGoal());
          double diff2 = Math.abs(dto2.getExpectedExpense() - dto2.getSpendingGoal());
          return Double.compare(diff2, diff1);
        });
  }

  /**
   * Maps a SavingChallengeEntity to a response-dto. Calls the findCurrentSpendingOnChallenge method
   * and adds the result to the dto.
   *
   * @param savingChallengeEntity The Saving challenge mapped to the dto
   * @return The dto
   */
  private SavingChallengeResponseDto mapSavingChallengeEntityToResponseDto(
      SavingChallengeEntity savingChallengeEntity) {
    SavingChallengeResponseDto dto = savingChallengeMapper.mapTo(savingChallengeEntity);
    double currentSpending = findCurrentSpendingOnChallenge(savingChallengeEntity);
    dto.setCurrentSpending(currentSpending);
    return dto;
  }

  /**
   * Retrieves the current spending for a specified saving challenge. This method calculates the
   * total amount spent on transactions belonging to a specific category and occurring within the
   * date range of the saving challenge.
   *
   * @param savingChallenge The saving challenge for which current spending is to be calculated.
   * @return The total spending as a double.
   * @throws ResponseStatusException if the saving challenge does not exist or the ID is not found.
   */
  private double findCurrentSpendingOnChallenge(SavingChallengeEntity savingChallenge) {
    BankAccountEntity sourceAccount = savingChallenge.getSavingGoal().getSourceBankAccount();

    return bankAccountService.getAmountUsedOnTransactionsOnCategoryBetweenDate(
        sourceAccount.getId(), savingChallenge.getTransactionCategory().getId(),
        savingChallenge.getStartDate().atStartOfDay(),
            savingChallenge.getEndingDate().atStartOfDay());
  }

  /**
   * Fetches a SavingChallengeEntity based on the provided challenge ID.
   *
   * @param savingChallengeId The ID of the saving challenge to retrieve.
   * @return The found SavingChallengeEntity.
   * @throws ResponseStatusException If no challenge could be found with the provided ID, with
   *     HttpStatus.NOT_FOUND.
   */
  private SavingChallengeEntity fetchChallenge(Long savingChallengeId) {
    return savingChallengeRepository
        .findById(savingChallengeId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Could not find saving challenge"));
  }

  /**
   * Checks if a saving challenge has ended based on the current date.
   *
   * @param savingChallenge The saving challenge entity to check.
   * @return true if the challenge has ended, false otherwise.
   */
  private boolean checkIfChallengeEnded(SavingChallengeEntity savingChallenge) {
    return savingChallenge.getEndingDate().isBefore(LocalDate.now());
  }

  /**
   * Checks if the spending goal of a challenge has been exceeded and updates the challenge state to
   * FAILED if so.
   *
   * @param savingChallenge The entity of the challenge to check.
   * @return true if the spending goal was exceeded and the challenge state was updated to FAILED,
   *     false otherwise.
   */
  private boolean checkIfSpendingChallengeFailed(
      double currentSpending, SavingChallengeEntity savingChallenge) {
    if (currentSpending > savingChallenge.getSpendingGoal()) {
      savingChallenge.setState(ChallengeState.FAILED);
      savingChallengeRepository.save(savingChallenge);
      return true;
    }

    return false;
  }

  /**
   * Transfers the saved amount from a saving challenge from the source bank account to the
   * destination bank account.
   *
   * @param responseDto The DTO of the challenge containing the details for the transfer.
   * @param savingChallenge The entity of the challenge from which funds are being transferred.
   * @throws IllegalArgumentException If there are insufficient funds in the source account to
   *     perform the transfer.
   */
  private void transferSavings(
      SavingChallengeResponseDto responseDto, SavingChallengeEntity savingChallenge) {
    double amountSaved = savingChallenge.getExpectedExpense() - responseDto.getCurrentSpending();
    responseDto.setAmountSaved(amountSaved);

    Long sourceAccountId = savingChallenge.getSavingGoal().getSourceBankAccount().getId();
    double currentBalance = bankAccountService.findOne(sourceAccountId).getSum();

    if (amountSaved > currentBalance) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Insufficient funds to perform the transfer.");
    }

    TransferDto transferDto =
        TransferDto.builder()
            .sum(responseDto.getAmountSaved())
            .sourceBankAccountId(sourceAccountId)
            .destinationBankAccountId(
                savingChallenge.getSavingGoal().getDestinationBankAccount().getId())
            .date(LocalDateTime.now())
            .build();

    bankAccountService.addTransferToAccounts(transferDto);
  }

  /**
   * Handles the completion of a saving challenge. If the challenge has ended and has not previously
   * been marked as failed, this method sets the challenge state to COMPLETED.
   *
   * @param savingChallenge The saving challenge entity whose end status is being checked and
   *     potentially updated.
   * @return A boolean that is true if the challenges state was changed and saved.
   */
  private boolean handleChallengeEnding(SavingChallengeEntity savingChallenge) {
    if (checkIfChallengeEnded(savingChallenge)) {
      savingChallenge.setState(ChallengeState.COMPLETED);
      savingChallengeRepository.save(savingChallenge);
      return true;
    }
    return false;
  }

  /**
   * Processes and maps a list of SavingChallengeEntities to a list of SavingChallengeResponseDtos.
   *
   * @param challenges The list of challenge entities to process.
   * @return A list of corresponding DTOs.
   */
  public List<SavingChallengeResponseDto> processChallenges(
      List<SavingChallengeEntity> challenges) {
    return challenges.stream().map(this::processChallenge).collect(Collectors.toList());
  }

  /**
   * Processes a given saving challenge entity to update its state based on current spending and
   * challenge conditions, and maps it to a response DTO. This method first converts a
   * SavingChallengeEntity to its corresponding response DTO form. It then checks if the challenge
   * is still in progress and updates its state if necessary based on the current spending. If the
   * state is updated (either to FAILED or COMPLETED), the entity is saved, and the DTO's state is
   * updated accordingly.
   *
   * @param challenge The saving challenge entity to be processed.
   * @return The response DTO with potentially updated state information.
   */
  private SavingChallengeResponseDto processChallenge(SavingChallengeEntity challenge) {
    SavingChallengeResponseDto dto = mapSavingChallengeEntityToResponseDto(challenge);
    if (challenge.getState().equals(ChallengeState.IN_PROGRESS)) {
      if (updateChallengeState(dto.getCurrentSpending(), challenge)) {
        savingChallengeRepository.save(challenge);
        dto.setState(challenge.getState());
      }
    }
    return dto;
  }

  /**
   * Updates the state of a saving challenge based on its current spending relative to its defined
   * goals. This method evaluates whether the challenge has failed due to exceeding spending limits
   * or if it should be marked as completed based on its ending conditions. The state of the
   * challenge is updated accordingly, and the method returns true if any state change occurs.
   *
   * @param currentSpending The current spending against the challenge's budget.
   * @param challenge The saving challenge entity whose state needs to be evaluated and possibly
   *     updated.
   * @return true if the challenge's state was updated, false otherwise.
   */
  private boolean updateChallengeState(double currentSpending, SavingChallengeEntity challenge) {
    if (checkIfSpendingChallengeFailed(currentSpending, challenge)) {
      challenge.setState(ChallengeState.FAILED);
      return true;
    } else if (handleChallengeEnding(challenge)) {
      challenge.setState(ChallengeState.COMPLETED);
      return true;
    }
    return false;
  }

  /**
   * Updates the linked saving goal's amount saved after completing a challenge.
   *
   * @param savingChallenge The challenge whose saving goal needs to be updated.
   */
  private void updateSavingGoalAfterChallengeTransfer(SavingChallengeEntity savingChallenge) {
    SavingGoalEntity linkedSavingGoal = savingChallenge.getSavingGoal();
    double previousAmountSaved = linkedSavingGoal.getAmountSaved();
    linkedSavingGoal.setAmountSaved(previousAmountSaved + savingChallenge.getAmountSaved());

    savingGoalRepository.save(linkedSavingGoal);
  }
}
