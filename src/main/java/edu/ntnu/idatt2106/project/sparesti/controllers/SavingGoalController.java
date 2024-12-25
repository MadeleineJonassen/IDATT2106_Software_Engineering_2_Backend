package edu.ntnu.idatt2106.project.sparesti.controllers;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoDetailsResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoGeneralResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoPost;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.SavingGoalService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Rest controller used for managing requests relating to saving goals. */
@RestController
@Log
public class SavingGoalController {

  private final SavingGoalService savingGoalService;
  private final UserInfoFromTokenService userInfoFromTokenService;

  public SavingGoalController(
      SavingGoalService savingGoalService, UserInfoFromTokenService userInfoFromTokenService) {
    this.savingGoalService = savingGoalService;
    this.userInfoFromTokenService = userInfoFromTokenService;
  }

  /**
   * Endpoint responsible for creating a new saving goal from given request data.
   *
   * @param savingGoalDto Data of the saving goal to be created
   * @param username Username of the user the goal should be assigned to
   * @return Simplified information about the created saving goal
   */
  @CrossOrigin("http://localhost:5173")
  @PostMapping("api/secure/users/{username}/goals")
  public ResponseEntity<SavingGoalDtoGeneralResponse> createSavingGoal(
      @RequestBody SavingGoalDtoPost savingGoalDto,
      @PathVariable String username,
      HttpServletRequest request) {

    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      SavingGoalDtoGeneralResponse createdSavingGoal =
          savingGoalService.createSavingGoal(savingGoalDto, username);
      return new ResponseEntity<>(createdSavingGoal, HttpStatus.CREATED);
    } catch (ResponseStatusException responseStatusException) {
      log.info(responseStatusException.getMessage());
      return new ResponseEntity<>(responseStatusException.getStatusCode());
    }
  }

  /**
   * Endpoint responsible for editing an existing saving goal.
   *
   * @param savingGoalDto The edited fields of the saving goal
   * @param username Username of the goal owner
   * @param goalId ID of the goal to edit
   * @return Information about the edited saving goal
   */
  @CrossOrigin("http://localhost:5173")
  @PatchMapping("api/secure/users/{username}/goals/{goalId}")
  public ResponseEntity<SavingGoalDtoGeneralResponse> editSavingGoal(
      @RequestBody SavingGoalDtoPost savingGoalDto,
      @PathVariable String username,
      @PathVariable String goalId,
      HttpServletRequest request) {

    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      SavingGoalDtoGeneralResponse editedSavingGoal =
          savingGoalService.editSavingGoal(savingGoalDto, username, Long.parseLong(goalId));
      return new ResponseEntity<>(editedSavingGoal, HttpStatus.OK);
    } catch (ResponseStatusException responseStatusException) {
      log.info(responseStatusException.getMessage());
      return new ResponseEntity<>(responseStatusException.getStatusCode());
    }
  }

  /**
   * Endpoint for getting all saving goals for a user.
   *
   * @param username Username of the user to check for
   * @return List of saving goal belonging to the user
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/users/{username}/goals")
  public ResponseEntity<List<SavingGoalDtoGeneralResponse>> getAllSavingGoals(
      @PathVariable String username, HttpServletRequest request) {

    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      List<SavingGoalDtoGeneralResponse> goalDtoGetList =
          savingGoalService.getAllSavingGoalsForUser(username);
      return new ResponseEntity<>(goalDtoGetList, HttpStatus.OK);
    } catch (ResponseStatusException e) {
      log.info(e.getMessage());
      return new ResponseEntity<>(e.getStatusCode());
    }
  }

  /**
   * Endpoint for getting detailed information about a saving goal, main difference from general
   * information being a list of saving challenges contained in the saving goal.
   *
   * @param goalId ID of the goal to check for
   * @return Detailed information about the saving goal
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/users/{username}/goals/{goalId}")
  public ResponseEntity<SavingGoalDtoDetailsResponse> getSavingGoal(
      @PathVariable String goalId, @PathVariable String username, HttpServletRequest request) {

    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    try {
      SavingGoalDtoDetailsResponse goalDtoGet =
          savingGoalService.getSavingGoal(Long.parseLong(goalId));
      return new ResponseEntity<>(goalDtoGet, HttpStatus.OK);
    } catch (ResponseStatusException e) {
      log.info(e.getMessage());
      return new ResponseEntity<>(e.getStatusCode());
    }
  }
}
