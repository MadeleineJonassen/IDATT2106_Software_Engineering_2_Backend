package edu.ntnu.idatt2106.project.sparesti.schedulers;

import edu.ntnu.idatt2106.project.sparesti.services.SavingGoalService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Schedules tasks related to the management of saving goals by leveraging the {@link
 * SavingGoalService}. This class is responsible for automating periodic updates to the state of
 * saving goals based on predefined criteria and timings.
 *
 * <p>Tasks within this scheduler are triggered automatically according to the specified cron
 * expression, ensuring consistent and timely updates to saving goals.
 */
@Component
public class SavingGoalTaskScheduler {

  private SavingGoalService savingGoalService;

  /**
   * Creates a new instance of SavingGoalTaskScheduler, initializing it with a service that handles
   * operations related to saving goals.
   *
   * @param savingGoalService The service responsible for managing saving goal operations, provided
   *     via dependency injection. This service is essential for the task execution logic
   *     encapsulated in this scheduler.
   */
  public SavingGoalTaskScheduler(SavingGoalService savingGoalService) {
    this.savingGoalService = savingGoalService;
  }

  /**
   * Executes a scheduled task to update the states of all saving goals. This method is invoked
   * automatically at 1 AM Oslo time daily.
   *
   * <p>The method calls the updateAllSavingGoalsStates method of the {@link SavingGoalService} to
   * perform necessary state updates on all saving goals managed within the system.
   */
  @Scheduled(cron = "0 0 1 * * ?", zone = "Europe/Oslo")
  public void updateSavingGoalsState() {
    savingGoalService.updateAllSavingGoalsStates();
  }
}
