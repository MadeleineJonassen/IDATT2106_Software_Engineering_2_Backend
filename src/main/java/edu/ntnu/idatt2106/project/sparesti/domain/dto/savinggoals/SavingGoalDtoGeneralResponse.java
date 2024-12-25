package edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dto representing a response object containing general information about a saving goal. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SavingGoalDtoGeneralResponse extends SavingGoalDto {
  private Long id;

  private BankAccountDto sourceBankAccount;

  private BankAccountDto destinationBankAccount;

  private double amountSaved;

  private GoalState state;
}
