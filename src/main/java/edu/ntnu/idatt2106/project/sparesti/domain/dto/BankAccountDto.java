package edu.ntnu.idatt2106.project.sparesti.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dto holding information about a bank account Object. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDto {
  private Long id;
  private Long userEntityId;
  private String name;
  private double sum;
}
