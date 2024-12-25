package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import edu.ntnu.idatt2106.project.sparesti.domain.enums.ChallengeState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An Entity holding information about a saving challenge Object. */
@Entity
@Table(name = "savings_challenge")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SavingChallengeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private Integer expectedExpense;

  private Integer spendingGoal;

  private LocalDate startDate;

  private Double amountSaved;

  private LocalDate endingDate;

  @Enumerated(EnumType.STRING)
  private ChallengeState state;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "savings_goal_id")
  private SavingGoalEntity savingGoal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_category_id")
  private TransactionCategoryEntity transactionCategory;
}
