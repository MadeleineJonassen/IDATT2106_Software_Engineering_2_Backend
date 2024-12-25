package edu.ntnu.idatt2106.project.sparesti.services;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetails;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetailsRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserLeaderboardDto;
import java.util.List;

/** Service interface for user-related operations. */
public interface UserService {
  /**
   * Service for adding a user to the database.
   *
   * @param userDto The UserDto object containing the user data to be added.
   * @return The added UserDto object.
   */
  UserDto createUser(UserDto userDto);

  /**
   * Service for checking if a user exists in the database.
   *
   * @param userDto The UserDto object to check for.
   * @return True if the user exists, False otherwise.
   */
  Boolean userExists(UserDto userDto);

  /**
   * Creates a new user and assigns default bank accounts to the user.
   *
   * @param userDto the UserDto object containing the new user's details.
   * @return the UserDto object after adding default bank accounts to the new user.
   */
  UserDto createUserWithDefaultBankAccounts(UserDto userDto);

  /**
   * Retrieves a list of all bank accounts associated with a specific username. This method is
   * responsible for fetching all the bank accounts linked to the given username. If no bank
   * accounts are found or the username does not exist, it returns an empty list.
   *
   * @param username the username whose bank accounts are to be retrieved.
   * @return a list of BankAccountDto objects representing the bank accounts of the user.
   */
  List<BankAccountDto> getAllBankAccounts(String username);

  /**
   * Gets details about a user.
   *
   * @param username Username to fetch data for
   * @return Detailed user information
   */
  UserDetails getUserDetails(String username);

  /**
   * Used to edit user details.
   *
   * @param username Username of the user to modify
   * @param userDetails Updated user detail fields
   * @return Updated user details
   */
  UserDetails editUser(String username, UserDetailsRequestDto userDetails);

  /**
   * Used for getting a leader board of users.
   *
   * @return list of user Dtos sorted by points (high to low).
   */
  public List<UserLeaderboardDto> getGlobalLeaderboard();

  /**
   * Method for gettung the sub id for a user.
   *
   * @param username the given user to get the sub id from.
   * @return the sub id string.
   */
  public String getUserFromSubId(String username);
}
