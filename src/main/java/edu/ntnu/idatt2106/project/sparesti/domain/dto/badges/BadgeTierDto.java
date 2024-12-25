package edu.ntnu.idatt2106.project.sparesti.domain.dto.badges;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dto representing information about a tier of a badge. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BadgeTierDto {
  private Long id;

  private String description;

  private Integer tier;

  private Integer target;
}
