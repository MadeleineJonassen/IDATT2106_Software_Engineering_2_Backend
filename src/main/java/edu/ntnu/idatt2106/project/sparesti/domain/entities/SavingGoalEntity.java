package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import edu.ntnu.idatt2106.project.sparesti.domain.enums.ChallengeState;
import edu.ntnu.idatt2106.project.sparesti.domain.enums.GoalState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An Entity holding information about a saving goal Object. */
@Entity
@Table(name = "savings_goal")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingGoalEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String title;

  private Integer goalSum;

  private LocalDate endingDate;

  private double amountSaved;

  @Enumerated(EnumType.STRING)
  private GoalState state;

  @Column(columnDefinition = "longtext")
  private String imageUrl;

  @OneToMany(
      mappedBy = "savingGoal",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  @Builder.Default
  private List<SavingChallengeEntity> savingChallenges = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_bank_account_id")
  private BankAccountEntity sourceBankAccount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_bank_account_id")
  private BankAccountEntity destinationBankAccount;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_entity_id")
  private UserEntity userEntity;
}
