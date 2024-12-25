package edu.ntnu.idatt2106.project.sparesti.domain.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Dto containing necessary request data for setting preferred accounts. */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsRequestDto extends UserDto {
  private Long preferredSavingsAccountId;

  private Long preferredCheckingAccountId;
}
