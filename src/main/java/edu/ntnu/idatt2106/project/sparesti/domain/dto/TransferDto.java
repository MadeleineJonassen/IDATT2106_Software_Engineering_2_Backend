package edu.ntnu.idatt2106.project.sparesti.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dto holding information about a transfer Object. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferDto {
  private Long id;
  private LocalDateTime date;
  private double sum;
  private String description;
  private Long sourceBankAccountId;
  private Long destinationBankAccountId;
}
