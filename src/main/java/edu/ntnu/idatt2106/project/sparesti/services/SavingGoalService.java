package edu.ntnu.idatt2106.project.sparesti.services;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoDetailsResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoGeneralResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoPost;
import java.util.List;

/**
 * Interface providing services between saving goal controller and saving goal related repository
 * operations.
 */
public interface SavingGoalService {

  /**
   * Service responsible for creating a new saving goal.
   *
   * @param savingGoalDto The saving goal which should be created
   * @param username Username of the user which owns the goal
   * @return The created Savings goal
   */
  SavingGoalDtoGeneralResponse createSavingGoal(SavingGoalDtoPost savingGoalDto, String username);

  /**
   * Service responsible for editing a given saving goal.
   *
   * @param savingGoalDto The saving goal with the fields which should be edited
   * @param username Username of the user which owns the goal
   * @param goalId ID of the goal which should be edited
   * @return The edited Savings goal
   */
  SavingGoalDtoGeneralResponse editSavingGoal(
      SavingGoalDtoPost savingGoalDto, String username, Long goalId);

  /**
   * Service responsible for fetching all saving goals for a user.
   *
   * @param username Username of the user to fetch for
   * @return List of saving goals belonging to user
   */
  List<SavingGoalDtoGeneralResponse> getAllSavingGoalsForUser(String username);

  /**
   * Service responsible for getting details about a savings goal.
   *
   * @param goalId ID of the goal to get details about
   * @return Detailed information about a savings goal
   */
  SavingGoalDtoDetailsResponse getSavingGoal(Long goalId);

  /**
   * Checks the progress and updates all Saving Goals that are currently in progress in Database.
   */
  void updateAllSavingGoalsStates();
}
