package edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A dto representing a saving goal, containing the necessary information to create a saving goal.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SavingGoalDtoPost extends SavingGoalDto {
  private Long sourceBankAccountId;

  private Long destinationBankAccountId;
}
