package edu.ntnu.idatt2106.project.sparesti.domain.dto.users;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeProgressDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.CompletedBadgeDto;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** A dto containing extra details about a user such as preferred accounts and badges. */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails extends UserDto {
  private BankAccountDto preferredSavingsAccount;

  private BankAccountDto preferredCheckingAccount;

  private List<BadgeProgressDto> badgeProgress;

  private List<CompletedBadgeDto> completedBadges;

  private Integer completedChallenges;

  private Integer completedGoals;

  private Integer currentStreak;

  private LocalDate lastCompletedChallengeDate;

  private LocalDate lastStreakDate;

  private Double totalSaved;
}
