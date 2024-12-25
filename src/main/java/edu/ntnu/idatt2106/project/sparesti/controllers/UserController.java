package edu.ntnu.idatt2106.project.sparesti.controllers;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetails;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetailsRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserLeaderboardDto;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.UserService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for user related operations.
 *
 * <p>This endpoint provides endpoints for registration of new users. //TODO legg til resten av det
 * vi tenker å ha her
 */
@Log
@CrossOrigin("http://localhost:5173")
@RequestMapping("api/secure/users")
@RestController
public class UserController {
  /** Used for Dependency Injection. */
  private final UserService userService;

  private final UserInfoFromTokenService userInfoFromTokenService;

  /**
   * Used for Dependency Injection.
   *
   * @param userService The injected UserService object.
   */
  public UserController(
      UserService userService, UserInfoFromTokenService userInfoFromTokenService) {
    this.userService = userService;
    this.userInfoFromTokenService = userInfoFromTokenService;
  }

  /**
   * Endpoint for checking if a user exists and save if not.
   *
   * @param userDto the user dto to potentially save.
   * @return the new saved user or an error response.
   */
  @PostMapping
  public ResponseEntity<UserDto> saveUser(
      @RequestBody UserDto userDto, HttpServletRequest request) {
    try {
      userDto.setSubId(userInfoFromTokenService.getSubFromAccessToken(request));
      UserDto savedUser = userService.createUserWithDefaultBankAccounts(userDto);
      return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    } catch (Exception e) {
      log.info("An internal server error occurred.");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred");
    }
  }

  /**
   * Handles the HTTP GET request to retrieve all bank accounts associated with a specific user.
   *
   * @param username the username of the user whose bank accounts are to be retrieved, obtained from
   *     the URL path.
   * @return a {@link ResponseEntity} object containing a list of {@link BankAccountDto} instances
   *     associated with the user and the HTTP status code. The status code returned is
   *     HttpStatus.OK (200) indicating a successful retrieval.
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("/{username}/accounts")
  public ResponseEntity<List<BankAccountDto>> getAllBankAccountsByUser(
      @PathVariable String username, HttpServletRequest request) {
    // Validate user authorization
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return new ResponseEntity<>(userService.getAllBankAccounts(username), HttpStatus.OK);
  }

  /**
   * Endpoint for getting detailed user information.
   *
   * @param username Username of the user to fetch information about
   * @param request Http request data including token
   * @return Detailed user information
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("/{username}")
  public ResponseEntity<UserDetails> getUserDetails(
      @PathVariable String username, HttpServletRequest request) {
    return new ResponseEntity<>(userService.getUserDetails(username), HttpStatus.OK);
  }

  /**
   * Endpoint for updating a given user.
   *
   * @param username Username of the user to update
   * @param userDetails New user information
   * @param request Http request data including token
   * @return Detailed user information for the edited user.
   */
  @CrossOrigin("http://localhost:5173")
  @PatchMapping("/{username}")
  public ResponseEntity<UserDto> updateUser(
      @PathVariable String username,
      @RequestBody UserDetailsRequestDto userDetails,
      HttpServletRequest request) {
    if (!userInfoFromTokenService.validateUserAuthorization(request, username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return new ResponseEntity<>(userService.editUser(username, userDetails), HttpStatus.OK);
  }

  /**
   * Endpoint for getting the global leaderboard of user based on points.
   *
   * @return list of users in point-descending order.
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("/leaderboard")
  public ResponseEntity<List<UserLeaderboardDto>> getGlobalLeaderboard() {
    return new ResponseEntity<>(userService.getGlobalLeaderboard(), HttpStatus.OK);
  }
}
