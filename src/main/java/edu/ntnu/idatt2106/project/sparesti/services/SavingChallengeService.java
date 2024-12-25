package edu.ntnu.idatt2106.project.sparesti.services;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeResponseDto;
import java.util.List;

/**
 * Interface providing services between saving challenge controller and saving challenge related
 * repository operations.
 */
public interface SavingChallengeService {

  /**
   * Service responsible for creating a new saving challenge as part of a savings goal.
   *
   * @param savingChallengeDto Dto containing necessary data to create saving challenge
   * @param goalId Id of the related saving goal
   * @return The created saving challenge with additional response data
   */
  public SavingChallengeResponseDto createSavingChallenge(
      SavingChallengeRequestDto savingChallengeDto, Long goalId);

  /**
   * Service responsible for getting a list of all challenges within a goal.
   *
   * @param goalId ID of the saving goal the get saving challenges for
   * @return A list of saving challenges part of the saving goal
   */
  public List<SavingChallengeResponseDto> getAndProcessAllChallengesInGoal(Long goalId);

  /**
   * Service responsible for getting all challenges in a user.
   *
   * @param username The username to the user to get all challenges from
   * @return A list of saving challenges dtos that correlate with the user
   */
  public List<SavingChallengeResponseDto> getAndProcessUserChallenges(String username);

  /**
   * Marks the challenge as done, and uses BankAccountService to transfer savings.
   *
   * @param savingChallengeId The challenge to complete
   * @return A dto with information about the challenge after it is complete
   */
  public SavingChallengeResponseDto finishChallenge(Long savingChallengeId, String username);

  /**
   * Retrieves and sorts a list of saving challenge suggestions for a specific saving goal. This
   * method fetches all available categories for the given user that do not have an ongoing
   * challenge, generates challenge suggestions for each, and sorts them by the difference between
   * expected expense and spending goal.
   *
   * @param goalId The ID of the saving goal for which to generate challenge suggestions.
   * @return A sorted list of SavingChallengeResponseDto objects representing the challenge
   *     suggestions.
   */
  public List<SavingChallengeResponseDto> getChallengeSuggestions(Long goalId);

  /**
   * Checks and updates the state of all the challenges in the database that are currently in the
   * Challenge-state of IN_PROGRESS.
   */
  void updateAllSavingChallengeStates();
}
