package edu.ntnu.idatt2106.project.sparesti.domain.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dto representing user data stored on the leaderboard. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLeaderboardDto extends UserDto {
  private Integer currentStreak;
}
