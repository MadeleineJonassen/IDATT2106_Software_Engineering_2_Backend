package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An Entity holding information about a user Object. */
@Getter
@Setter
@Builder
@Entity
@Table(name = "user_entity")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String email;

  private String fullName;
  private String username;
  private String imageUrl;
  private int score;
  private String subId;

  private Integer completedChallenges;

  private Integer completedGoals;

  private Integer currentStreak;

  private LocalDate lastCompletedChallengeDate;

  private LocalDate lastStreakDate;

  private Double totalSaved;

  @OneToMany(
      mappedBy = "userEntity",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  @Builder.Default
  private List<SavingGoalEntity> savingGoals = new ArrayList<>();

  @OneToMany(
      mappedBy = "userEntity",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<BankAccountEntity> bankAccounts;

  @OneToOne
  @JoinColumn(name = "preferred_savings_account_id", referencedColumnName = "id")
  private BankAccountEntity preferredSavingsAccount;

  @OneToOne
  @JoinColumn(name = "preferred_checking_account_id", referencedColumnName = "id")
  private BankAccountEntity preferredCheckingAccount;

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<BadgeProgressEntity> badgeProgress;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<CompletedBadgeEntity> completedBadges;

  /**
   * Adds a bank account to the list of bank accounts for this user. If the list is null, it
   * initializes a new list before adding the bank account. Sets the user reference in the bank
   * account object to this user.
   *
   * @param bankAccount The bank account to add to this user.
   */
  public void addBankAccount(BankAccountEntity bankAccount) {
    if (bankAccounts == null) {
      bankAccounts = new ArrayList<>();
    }
    bankAccounts.add(bankAccount);
    bankAccount.setUserEntity(this);
  }

  /**
   * Adds a badge currently in progress to a list of in progress badges.
   *
   * @param badgeProgressEntity The badge to add to progress tracking
   */
  public void addBadgeProgress(BadgeProgressEntity badgeProgressEntity) {
    if (this.badgeProgress == null) {
      this.badgeProgress = new ArrayList<>();
    }
    this.badgeProgress.add(badgeProgressEntity);
  }

  /**
   * Gets progress data about a specific badge.
   *
   * @param badgeId ID of the badge to check for
   * @return Found Badge progress entity with progress data
   */
  public BadgeProgressEntity getBadgeProgressEntity(Long badgeId) {
    Optional<BadgeProgressEntity> foundBadgeProgressEntity =
        this.badgeProgress.stream()
            .filter(
                badgeProgressEntity -> badgeProgressEntity.getBadgeEntity().getId().equals(badgeId))
            .findFirst();
    return foundBadgeProgressEntity.orElse(null);
  }

  /**
   * Gets progress data about a badge owned by the user.
   *
   * @param name Name of the badge
   * @return Progress data about the badge
   */
  public BadgeProgressEntity getBadgeProgressEntityByName(String name) {
    Optional<BadgeProgressEntity> foundBadgeProgressEntity =
        this.badgeProgress.stream()
            .filter(
                badgeProgressEntity -> badgeProgressEntity.getBadgeEntity().getName().equals(name))
            .findFirst();
    return foundBadgeProgressEntity.orElse(null);
  }

  /**
   * Used to add a completed badge to a user.
   *
   * @param completedBadge Badge to add
   */
  public void addCompletedBadge(CompletedBadgeEntity completedBadge) {
    if (this.completedBadges == null) {
      this.completedBadges = new ArrayList<>();
    }
    this.completedBadges.add(completedBadge);
  }

  public void increaseCompletedChallenges() {
    this.completedChallenges += 1;
  }

  public void increaseCompletedGoals() {
    this.completedGoals += 1;
  }

  public void increaseStreak() {
    this.currentStreak += 1;
  }

  public void resetStreak() {
    this.currentStreak = 0;
  }

  public void increaseTotalSaved(Double moneyToAdd) {
    this.totalSaved += moneyToAdd;
  }
}
