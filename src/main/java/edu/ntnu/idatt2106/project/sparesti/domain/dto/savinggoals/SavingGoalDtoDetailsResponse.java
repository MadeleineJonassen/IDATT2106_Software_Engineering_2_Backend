package edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeResponseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dto representing a response object containing detailed information about a saving goal. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SavingGoalDtoDetailsResponse extends SavingGoalDto {
  private Long id;

  private BankAccountDto sourceBankAccount;

  private BankAccountDto destinationBankAccount;

  private List<SavingChallengeResponseDto> savingChallenges;

  private double amountSaved;

  private GoalState state;
}
