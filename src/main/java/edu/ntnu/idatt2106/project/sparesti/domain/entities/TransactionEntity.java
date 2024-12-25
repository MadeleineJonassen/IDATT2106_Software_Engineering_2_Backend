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

/** An Entity holding information about a transaction Object. */
@Entity
@Getter
@Setter
@Builder
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String description;

  private double sum;

  private LocalDateTime date;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_category_id")
  private TransactionCategoryEntity transactionCategoryEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bank_account_id")
  private BankAccountEntity bankAccount;
}
