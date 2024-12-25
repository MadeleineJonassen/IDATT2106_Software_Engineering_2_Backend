package edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** A dto representing the request object sent by users to create. */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SavingChallengeRequestDto extends SavingChallengeDto {

  private Long categoryId;
}
