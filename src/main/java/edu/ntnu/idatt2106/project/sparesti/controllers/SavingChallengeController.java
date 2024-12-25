package edu.ntnu.idatt2106.project.sparesti.controllers;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeResponseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoDetailsResponse;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.SavingChallengeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Rest controller responsible for handling requests related to saving challenges. */
@RestController
@Log
public class SavingChallengeController {
  private final SavingChallengeService savingChallengeService;

  private UserInfoFromTokenService userInfoFromTokenService;

  public SavingChallengeController(
      SavingChallengeService savingChallengeService,
      UserInfoFromTokenService userInfoFromTokenService) {
    this.savingChallengeService = savingChallengeService;
    this.userInfoFromTokenService = userInfoFromTokenService;
  }

  /**
   * Endpoint responsible for creating a new saving challenge and assigning it to a saving goal.
   *
   * @param savingChallengeDto The saving challenge to be created
   * @param goalId ID of the goal to assign the challenge to
   * @return The created saving challenge
   */
  @CrossOrigin("http://localhost:5173")
  @PostMapping("api/secure/users/{username}/goals/{goalId}/challenges")
  public ResponseEntity<SavingChallengeResponseDto> createSavingChallenge(
      @RequestBody SavingChallengeRequestDto savingChallengeDto,
      @PathVariable String goalId,
      @PathVariable String username,
      HttpServletRequest request) {

    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    SavingChallengeResponseDto createdChallenge =
        savingChallengeService.createSavingChallenge(savingChallengeDto, Long.parseLong(goalId));
    return new ResponseEntity<>(createdChallenge, HttpStatus.CREATED);
  }

  /**
   * Gets only a list of challenges in a savings goal, with no other information about the goal.
   *
   * @param goalId ID of the goal to get challenges for
   * @return List of saving challenges part of the specified saving goal
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/users/{username}/goals/{goalId}/challenges")
  public ResponseEntity<List<SavingChallengeResponseDto>> getAllChallengesInGoal(
      @PathVariable String goalId, @PathVariable String username, HttpServletRequest request) {

    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return new ResponseEntity<>(
        savingChallengeService.getAndProcessAllChallengesInGoal(Long.parseLong(goalId)),
        HttpStatus.OK);
  }

  /**
   * Gets every saving challenge in a specific user, with no other information about the goal.
   *
   * @param username the user to get all saving challenges from.
   * @return a list with saving challenge response dto's.
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/users/{username}/challenges")
  public ResponseEntity<List<SavingChallengeResponseDto>> getAllChallengesInUser(
      @PathVariable String username, HttpServletRequest request) {

    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    try {
      List<SavingChallengeResponseDto> challenges =
          savingChallengeService.getAndProcessUserChallenges(username);
      return ResponseEntity.ok(challenges);
    } catch (Exception e) {
      log.info(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Processes the challenge as complete through the SavingChallengeService.
   *
   * @param challengeId the id for the challenge to complete.
   * @return a response dto containing data about the completed challenge, including the amount
   *     saved throughout the challenge.
   */
  @CrossOrigin("http://localhost:5173")
  @PostMapping("api/secure/users/{username}/{challengeId}/transfer")
  public ResponseEntity<SavingChallengeResponseDto> completeChallenge(
      @PathVariable String username, @PathVariable Long challengeId, HttpServletRequest request) {
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return new ResponseEntity<>(
        savingChallengeService.finishChallenge(challengeId, username), HttpStatus.OK);
  }

  /**
   * Handles the HTTP GET request to retrieve saving challenge suggestions for a specific user and
   * goal.
   *
   * @param username The username as extracted from the path variable.
   * @param goalId The goal ID as extracted from the path variable.
   * @param request The HttpServletRequest providing request information for HTTP servlets.
   * @return A ResponseEntity containing a list of SavingChallengeRequestDto objects if the user is
   *     authorized; otherwise, returns a ResponseEntity with HTTP status FORBIDDEN.
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/users/{username}/goals/{goalId}/suggestions")
  public ResponseEntity<List<SavingChallengeResponseDto>> getSavingChallengeSuggestion(
      @PathVariable String username, @PathVariable Long goalId, HttpServletRequest request) {
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return new ResponseEntity<>(
        savingChallengeService.getChallengeSuggestions(goalId), HttpStatus.OK);
  }
}
