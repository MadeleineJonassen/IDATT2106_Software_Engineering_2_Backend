package edu.ntnu.idatt2106.project.sparesti.schedulers;

import edu.ntnu.idatt2106.project.sparesti.services.SavingChallengeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for scheduling tasks related to the management of saving challenges. It
 * utilizes the {@link SavingChallengeService} to perform operations on saving challenges at
 * scheduled intervals.
 *
 * <p>The scheduled tasks are defined within this class and are meant to automate regular updates
 * and checks that need to be performed on the saving challenges.
 */
@Component
public class SavingChallengeTaskScheduler {
  private SavingChallengeService savingChallengeService;

  /**
   * Constructs a new SavingChallengeTaskScheduler with the necessary service to operate on saving
   * challenges.
   *
   * @param savingChallengeService The service responsible for managing saving challenge operations,
   *     provided via dependency injection.
   */
  public SavingChallengeTaskScheduler(SavingChallengeService savingChallengeService) {
    this.savingChallengeService = savingChallengeService;
  }

  /**
   * Scheduled task that triggers the update of all saving challenge states. This method is executed
   * automatically at 2 AM Oslo time every day.
   */
  @Scheduled(cron = "0 0 2 * * ?", zone = "Europe/Oslo")
  public void updateSavingChallengesState() {
    savingChallengeService.updateAllSavingChallengeStates();
  }
}
