package edu.ntnu.idatt2106.project.sparesti.controllers;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeProgressDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.CompletedBadgeDto;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.BadgeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller used for managing requests relating to badges. */
@RestController
public class BadgeController {
  private BadgeService badgeService;
  private final UserInfoFromTokenService userInfoFromTokenService;

  public BadgeController(
      BadgeService badgeService, UserInfoFromTokenService userInfoFromTokenService) {
    this.userInfoFromTokenService = userInfoFromTokenService;
    this.badgeService = badgeService;
  }

  /**
   * Endpoint that allows for fetching all badges owned by a user.
   *
   * @param username Username of the user to fetch for
   * @return List of found badges
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/users/{username}/badges")
  public ResponseEntity<List<CompletedBadgeDto>> getBadgesByUser(
      @PathVariable String username, HttpServletRequest request) {
    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return new ResponseEntity<>(badgeService.getBadgesByUser(username), HttpStatus.OK);
  }

  /**
   * Endpoint that allows for fetching all badges that exist.
   *
   * @return List of badges
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/badges")
  public ResponseEntity<List<BadgeDto>> getAllBadges() {
    return new ResponseEntity<>(badgeService.getAllBadges(), HttpStatus.OK);
  }

  /**
   * Endpoint that allows for fetching a user's progress across all badges.
   *
   * @param username Username of the user to fetch for
   * @param request Http request data
   * @return List containing a user's badge progress
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/users/{username}/badges/progress")
  public ResponseEntity<List<BadgeProgressDto>> getAllBadgeProgress(
      @PathVariable String username, HttpServletRequest request) {
    return new ResponseEntity<>(badgeService.getAllBadgeProgress(username), HttpStatus.OK);
  }
}
