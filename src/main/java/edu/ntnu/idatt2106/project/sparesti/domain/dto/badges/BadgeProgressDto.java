package edu.ntnu.idatt2106.project.sparesti.domain.dto.badges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dto representing user progress on a badge. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BadgeProgressDto {
  private Long id;

  private Integer progress;

  private BadgeDto badgeDto;
}
