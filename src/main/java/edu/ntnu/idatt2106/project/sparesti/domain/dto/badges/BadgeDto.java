package edu.ntnu.idatt2106.project.sparesti.domain.dto.badges;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeTierEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dto holding information about a badge Object. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BadgeDto {
  private Long id;

  private String name;

  private String description;

  private List<BadgeTierDto> badgeTiers;
}
