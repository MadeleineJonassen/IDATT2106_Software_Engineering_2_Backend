package edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.ChallengeState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** A dto representing the created saving challenge sent in response to requests. */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SavingChallengeResponseDto extends SavingChallengeDto {
  private Long id;

  private TransactionCategoryDto transactionCategory;

  private ChallengeState state;

  private double currentSpending;

  private double amountSaved;
}
