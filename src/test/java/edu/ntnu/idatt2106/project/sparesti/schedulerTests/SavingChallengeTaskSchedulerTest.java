package edu.ntnu.idatt2106.project.sparesti.schedulerTests;

import edu.ntnu.idatt2106.project.sparesti.schedulers.SavingChallengeTaskScheduler;
import edu.ntnu.idatt2106.project.sparesti.services.SavingChallengeService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.verify;

/** Tests the SavingChallengeTaskScheduler. */
@SpringBootTest
@ActiveProfiles("test")
public class SavingChallengeTaskSchedulerTest {

  @Mock private SavingChallengeService savingChallengeService;

  @InjectMocks private SavingChallengeTaskScheduler scheduler;

  /**
   * Test method that verifies that updateAllSavingChallengeStates is run when the scheduler tries
   * to update the saving challenges state.
   */
  @Test
  public void testUpdateSavingGoalsState() {
    scheduler.updateSavingChallengesState();

    verify(savingChallengeService).updateAllSavingChallengeStates();
  }
}
