package edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dto holding information about a saving goal Object. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SavingGoalDto {
  private String title;

  private Integer goalSum;

  private LocalDate endingDate;

  private String imageUrl;
}
