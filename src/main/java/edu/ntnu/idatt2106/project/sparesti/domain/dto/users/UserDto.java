package edu.ntnu.idatt2106.project.sparesti.domain.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** A dto holding information about a user Object. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserDto {

  /** The id field represents the UID provided by Auth0. */
  private Long id;

  /** The username field represents the username. */
  private String username;

  /** The email field represents the users email. */
  private String email;

  /** The full name field represents the users full name. */
  private String fullName;

  /** The image url field the represents the profile picture url. */
  private String imageUrl;

  /** The score field represents the users score. */
  private int score;

  /** The sub id for each specific user. */
  private String subId;
}
