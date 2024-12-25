package edu.ntnu.idatt2106.project.sparesti.services;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeProgressDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.CompletedBadgeDto;
import java.util.List;

/**
 * Interface providing services between badge controller and badge repository related operations.
 */
public interface BadgeService {
  /**
   * Service that finds all badges registered on a user.
   *
   * @param username Username of the user to check for
   * @return List of found badges
   */
  public List<CompletedBadgeDto> getBadgesByUser(String username);

  /**
   * Service that finds all badges.
   *
   * @return List of badges
   */
  public List<BadgeDto> getAllBadges();

  /**
   * Service that finds progress of all badges on user.
   *
   * @param username of the user to check for
   * @return List of badges with progress
   */
  public List<BadgeProgressDto> getAllBadgeProgress(String username);
}
