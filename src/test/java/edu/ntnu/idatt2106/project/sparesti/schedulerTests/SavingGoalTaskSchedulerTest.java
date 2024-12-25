package edu.ntnu.idatt2106.project.sparesti.schedulerTests;

import edu.ntnu.idatt2106.project.sparesti.schedulers.SavingGoalTaskScheduler;
import edu.ntnu.idatt2106.project.sparesti.services.SavingGoalService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.verify;

/** Tests the SavingGoalTaskScheduler. */
@SpringBootTest
@ActiveProfiles("test")
public class SavingGoalTaskSchedulerTest {

  @Mock private SavingGoalService savingGoalService;

  @InjectMocks private SavingGoalTaskScheduler taskScheduler;

  /**
   * Test method that verifies that updateAllSavingGoalStates is run when the scheduler tries to
   * update the saving goals state.
   */
  @Test
  public void testUpdateSavingGoalsState() {
    taskScheduler.updateSavingGoalsState();

    verify(savingGoalService).updateAllSavingGoalsStates();
  }
}
