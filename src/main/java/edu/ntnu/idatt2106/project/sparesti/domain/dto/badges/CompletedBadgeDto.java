package edu.ntnu.idatt2106.project.sparesti.domain.dto.badges;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dto representing a completed badge. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompletedBadgeDto {
  private Long id;

  private String name;

  private String description;

  private Integer tier;
}
