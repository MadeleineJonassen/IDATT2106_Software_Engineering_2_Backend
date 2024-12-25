package edu.ntnu.idatt2106.project.sparesti.services.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoDetailsResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoGeneralResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoPost;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeProgressEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeTierEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.CompletedBadgeEntity;
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
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Implementation of the SavingGoalService interface. */
@Service
@Log
public class SavingGoalServiceImpl implements SavingGoalService {

  private SavingGoalRepository savingGoalRepository;

  private UserRepository userRepository;

  private BankAccountRepository bankAccountRepository;

  private BadgeRepository badgeRepository;

  private Mapper<SavingGoalEntity, SavingGoalDtoGeneralResponse> savingGoalMapper;

  /**
   * Constructor for the SavingGoalServiceImpl, responsible for injecting necessary dependencies.
   *
   * @param userRepository Repository containing user data
   * @param bankAccountRepository Repository containing bank account data
   * @param savingGoalMapper Mapper responsible for converting between saving goal dtos and entities
   * @param savingGoalRepository Repository containing saving goal data
   */
  public SavingGoalServiceImpl(
      UserRepository userRepository,
      BankAccountRepository bankAccountRepository,
      Mapper<SavingGoalEntity, SavingGoalDtoGeneralResponse> savingGoalMapper,
      SavingGoalRepository savingGoalRepository,
      BadgeRepository badgeRepository) {
    this.userRepository = userRepository;
    this.bankAccountRepository = bankAccountRepository;
    this.savingGoalMapper = savingGoalMapper;
    this.savingGoalRepository = savingGoalRepository;
    this.badgeRepository = badgeRepository;
  }

  @Override
  public SavingGoalDtoGeneralResponse createSavingGoal(
      SavingGoalDtoPost savingGoalDto, String username) {
    // Checks if any fields are undefined or illegal value, date and id does not need to be defined
    if (savingGoalDto.getGoalSum() == null
        || savingGoalDto.getGoalSum() <= 0
        || savingGoalDto.getTitle() == null
        || savingGoalDto.getTitle().isEmpty()
        || username == null
        || savingGoalDto.getDestinationBankAccountId() == null
        || savingGoalDto.getSourceBankAccountId() == null
        || savingGoalDto.getEndingDate() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing fields.");
    }

    SavingGoalEntity savingGoalEntity = createEntityFromDto(savingGoalDto, username);
    log.info("Goal entity: " + savingGoalEntity.getSourceBankAccount().getId());
    SavingGoalEntity createdGoal = savingGoalRepository.save(savingGoalEntity);
    log.info("Saved goal account: " + createdGoal.getSourceBankAccount().getId());

    SavingGoalDtoGeneralResponse createdGoalDto = savingGoalMapper.mapTo(createdGoal);
    log.info("Created dto goal: " + createdGoalDto.getSourceBankAccount());
    return createdGoalDto;
  }

  @Override
  public SavingGoalDtoGeneralResponse editSavingGoal(
      SavingGoalDtoPost savingGoalDto, String username, Long goalId) {
    if (username == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No defined user.");
    }

    if (savingGoalDto.getSourceBankAccountId() != null
        && savingGoalDto.getDestinationBankAccountId() != null) {
      if (savingGoalDto
          .getSourceBankAccountId()
          .equals(savingGoalDto.getDestinationBankAccountId())) {
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN, "Cannot set sender and receiver account to the same account.");
      }
    }

    if (!findUserEntity(username).getSavingGoals().contains(findSavingGoal(goalId))) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "You are not allowed to modify this goal.");
    }
    SavingGoalEntity originalGoal = findSavingGoal(goalId);

    if (savingGoalDto.getTitle() != null && !savingGoalDto.getTitle().isEmpty()) {
      originalGoal.setTitle(savingGoalDto.getTitle());
    }

    if (savingGoalDto.getGoalSum() != null && savingGoalDto.getGoalSum() > 0) {
      originalGoal.setGoalSum(savingGoalDto.getGoalSum());
    }

    if (savingGoalDto.getEndingDate() != null) {
      originalGoal.setEndingDate(savingGoalDto.getEndingDate());
    }

    if (savingGoalDto.getDestinationBankAccountId() != null) {
      if (findUserEntity(username)
          .getBankAccounts()
          .contains(findBankAccountEntity(savingGoalDto.getDestinationBankAccountId()))) {
        originalGoal.setDestinationBankAccount(
            findBankAccountEntity(savingGoalDto.getDestinationBankAccountId()));
      } else {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "You do not have access to this account.");
      }
    }

    if (savingGoalDto.getSourceBankAccountId() != null) {
      if (findUserEntity(username)
          .getBankAccounts()
          .contains(findBankAccountEntity(savingGoalDto.getSourceBankAccountId()))) {
        originalGoal.setSourceBankAccount(
            findBankAccountEntity(savingGoalDto.getSourceBankAccountId()));
      } else {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "You do not have access to this account.");
      }
    }

    if (findSavingGoal(goalId)
        .getSourceBankAccount()
        .getId()
        .equals(findSavingGoal(goalId).getDestinationBankAccount().getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Cannot set sender and receiver to same account.");
    }

    updateSavingGoalStateIfNeeded(originalGoal);

    return savingGoalMapper.mapTo(savingGoalRepository.save(originalGoal));
  }

  @Override
  public List<SavingGoalDtoGeneralResponse> getAllSavingGoalsForUser(String username) {
    UserEntity userEntity = findUserEntity(username);
    List<SavingGoalEntity> savingGoalEntities = userEntity.getSavingGoals();
    List<SavingGoalDtoGeneralResponse> savingGoalDtos =
        savingGoalEntities.stream()
            .map(
                savingGoalEntity -> {
                  if (updateSavingGoalStateIfNeeded(savingGoalEntity)) {
                    savingGoalRepository.save(savingGoalEntity);
                  }
                  return savingGoalMapper.mapTo(savingGoalEntity);
                })
            .collect(Collectors.toList());

    return savingGoalDtos;
  }

  @Override
  public SavingGoalDtoDetailsResponse getSavingGoal(Long goalId) {
    SavingGoalEntity savingGoalEntity =
        savingGoalRepository
            .findById(goalId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No goal with that id."));

    if (updateSavingGoalStateIfNeeded(savingGoalEntity)) {
      savingGoalRepository.save(savingGoalEntity);
    }

    SavingGoalDtoDetailsResponse savingGoalDto =
        new ModelMapper().map(savingGoalEntity, SavingGoalDtoDetailsResponse.class);
    return savingGoalDto;
  }

  @Override
  @Transactional
  public void updateAllSavingGoalsStates() {
    List<SavingGoalEntity> allSavingGoalsInDb = savingGoalRepository.findAll();

    for (SavingGoalEntity savingGoal : allSavingGoalsInDb) {
      if (updateSavingGoalStateIfNeeded(savingGoal)) {
        savingGoalRepository.save(savingGoal);
      }
    }
  }

  /**
   * Updates the state of a given saving goal if certain conditions are met. This method checks if
   * the goal is in progress and updates its state to either COMPLETED or FAILED based on the
   * current date and the goal's financial targets.
   *
   * <p>- A goal is marked as COMPLETED if the amount saved is greater than or equal to the goal
   * sum. - A goal is marked as FAILED if the current date is past the goal's ending date.
   *
   * <p>Also updates the correlating saving challenges that are still in progress.
   *
   * @param savingGoal the saving goal entity whose state may need updating
   * @return true if the state was updated, false otherwise
   */
  private boolean updateSavingGoalStateIfNeeded(SavingGoalEntity savingGoal) {
    if (savingGoal.getState().equals(GoalState.IN_PROGRESS)) {
      if (savingGoal.getGoalSum() <= savingGoal.getAmountSaved()) {
        UserEntity userEntity = savingGoal.getUserEntity();
        BadgeEntity badgeEntity1 = badgeRepository.findBadgeEntityByName("Supersparings-gris");
        BadgeEntity badgeEntity2 = badgeRepository.findBadgeEntityByName("Svinerik");

        if (userEntity.getBadgeProgressEntityByName("Svinerik") == null) {
          BadgeProgressEntity badgeProgressEntity =
              BadgeProgressEntity.builder()
                  .userEntity(userEntity)
                  .badgeEntity(badgeEntity2)
                  .build();

          CompletedBadgeEntity completedBadgeEntity =
              CompletedBadgeEntity.builder()
                  .name(badgeEntity2.getName())
                  .description(badgeEntity2.getDescription())
                  .build();
          userEntity.addBadgeProgress(badgeProgressEntity);
          userEntity.addCompletedBadge(completedBadgeEntity);
        }

        if (userEntity.getBadgeProgressEntityByName("Supersparings-gris") == null) {
          BadgeProgressEntity badgeProgressEntity =
              BadgeProgressEntity.builder()
                  .userEntity(userEntity)
                  .progress(1)
                  .badgeEntity(badgeEntity1)
                  .build();
          userEntity.addBadgeProgress(badgeProgressEntity);
        } else {
          userEntity.getBadgeProgressEntityByName("Supersparings-gris").increaseProgress();
        }

        for (BadgeTierEntity badgeTier : badgeEntity1.getBadgeTiers()) {
          CompletedBadgeEntity completedBadge =
              CompletedBadgeEntity.builder()
                  .name(badgeEntity1.getName())
                  .tier(badgeTier.getTier())
                  .description(badgeTier.getDescription())
                  .build();
          if (userEntity.getBadgeProgressEntityByName("Supersparings-gris").getProgress()
                  >= badgeTier.getTarget()
              && !userEntity.getCompletedBadges().stream()
                  .anyMatch(
                      completed ->
                          completed.getName().equals(badgeEntity1.getName())
                              && completed.getTier().equals(badgeTier.getTier()))) {
            userEntity.addCompletedBadge(completedBadge);
          }
        }
        userEntity.increaseCompletedGoals();
        savingGoal.setState(GoalState.COMPLETED);
        updateChallengesInProgress(savingGoal.getSavingChallenges(), ChallengeState.COMPLETED);
        return true;
      } else if (savingGoal.getEndingDate().isBefore(LocalDate.now())) {
        savingGoal.setState(GoalState.FAILED);
        updateChallengesInProgress(savingGoal.getSavingChallenges(), ChallengeState.FAILED);
        return true;
      }
    }
    return false;
  }

  /**
   * Updates all challenges that are still in progress to a certain ChallengeState.
   *
   * @param savingChallenges The challenges to update.
   * @param challengeState The state to update the challenges to.
   */
  private void updateChallengesInProgress(
      List<SavingChallengeEntity> savingChallenges, ChallengeState challengeState) {
    for (SavingChallengeEntity savingChallenge : savingChallenges) {
      if (savingChallenge.getState().equals(ChallengeState.IN_PROGRESS)) {
        savingChallenge.setState(challengeState);
      }
    }
  }

  /**
   * Helper method used to build a saving goal entity from provided info. Does not save the saving
   * goal in the database.
   *
   * @param savingGoalDto Information about the saving goal to build
   * @param username Username of the user associated with the saving goal
   * @return The built saving goal entity
   */
  private SavingGoalEntity createEntityFromDto(SavingGoalDtoPost savingGoalDto, String username) {

    UserEntity userEntity = findUserEntity(username);

    BankAccountEntity destinationAccount =
        findBankAccountEntity(savingGoalDto.getDestinationBankAccountId());
    if (!userEntity.getBankAccounts().contains(destinationAccount)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have access to that account.");
    }

    BankAccountEntity senderAccount = findBankAccountEntity(savingGoalDto.getSourceBankAccountId());
    if (!userEntity.getBankAccounts().contains(senderAccount)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You do not have access to that account.");
    }
    // Create saving goal entity.
    SavingGoalEntity savingGoalEntity =
        SavingGoalEntity.builder()
            .title(savingGoalDto.getTitle())
            .goalSum(savingGoalDto.getGoalSum())
            .endingDate(savingGoalDto.getEndingDate())
            .savingChallenges(new ArrayList<>())
            .sourceBankAccount(senderAccount)
            .destinationBankAccount(destinationAccount)
            .userEntity(userEntity)
            .state(GoalState.IN_PROGRESS)
            .build();

    return savingGoalEntity;
  }

  /**
   * Helper method to find a bank account entity from a provided account id.
   *
   * @param accountId ID of the account to look for
   * @return The found BankAccountEntity
   */
  private BankAccountEntity findBankAccountEntity(Long accountId) {
    return bankAccountRepository
        .findById(accountId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account with that id."));
  }

  /**
   * Helper method to find a user entity from a provided user id.
   *
   * @param userId ID of the user entity to look for
   * @return The found UserEntity
   */
  @Deprecated
  private UserEntity findUserEntity(Long userId) {
    log.info("user id: " + userId);
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that id."));
  }

  /**
   * Helper method to find a user entity from a provided username.
   *
   * @param username Username of the user to look for
   * @return The found UserEntity
   */
  private UserEntity findUserEntity(String username) {
    return userRepository
        .findUserEntityByUsername(username)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that username."));
  }

  /**
   * Helper method to find a saving goal given a goalId.
   *
   * @param goalId ID of the saving goal to look for
   * @return The found SavingGoalEntity
   */
  private SavingGoalEntity findSavingGoal(Long goalId) {
    return savingGoalRepository
        .findById(goalId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No goal with that id."));
  }
}
