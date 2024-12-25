package edu.ntnu.idatt2106.project.sparesti.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dto holding information about a transaction Object. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
  private Long id;
  private LocalDateTime date;
  private double sum;
  private String description;
  private Long bankAccountEntityId;
  private Long categoryEntityId;
}
