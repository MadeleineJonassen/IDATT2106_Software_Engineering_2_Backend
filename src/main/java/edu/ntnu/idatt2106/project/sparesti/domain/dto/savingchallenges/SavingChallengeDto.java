package edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** A dto holding information about a saving challenge object. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SavingChallengeDto {
  private Integer expectedExpense;

  private Integer spendingGoal;

  private LocalDate startDate;

  private LocalDate endingDate;
}
