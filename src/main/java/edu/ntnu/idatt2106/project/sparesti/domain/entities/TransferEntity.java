package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An Entity holding information about a transfer Object. */
@Entity
@Getter
@Setter
@Builder
@Table(name = "money_transfer")
@AllArgsConstructor
@NoArgsConstructor
public class TransferEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private LocalDateTime date;

  private String description;

  private double sum;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_bank_account_id")
  private BankAccountEntity sourceBankAccount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_bank_account_id")
  private BankAccountEntity destinationBankAccount;
}
