package edu.ntnu.idatt2106.project.sparesti.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for carrying projected daily expense information. This class is used
 * to encapsulate the data related to the expected daily expense associated with a particular
 * account and category.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectedDailyExpenseDto {
  private double projectedExpense;
}
