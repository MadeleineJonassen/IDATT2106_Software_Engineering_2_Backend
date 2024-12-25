package edu.ntnu.idatt2106.project.sparesti.security;

import edu.ntnu.idatt2106.project.sparesti.services.UserService;
import edu.ntnu.idatt2106.project.sparesti.services.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for extracting and verifying user information based on an OAuth2 access
 * token. This service handles JWT decoding to authenticate users by comparing the sub claim from
 * the token with stored user information.
 */
@Service
public class UserInfoFromTokenService {
  private final JwtDecoder jwtDecoder;
  private final UserService userService;

  /**
   * Constructs an instance of UserInfoFromTokenService with the specified JWT decoder and user
   * service.
   *
   * @param jwtDecoder The JwtDecoder used for decoding JWT tokens.
   * @param userService The UserService used for retrieving user details.
   */
  @Autowired
  public UserInfoFromTokenService(JwtDecoder jwtDecoder, UserServiceImpl userService) {
    this.jwtDecoder = jwtDecoder;
    this.userService = userService;
  }

  /**
   * Validates the authorization of a user making a request, by comparing the extracted sub claim
   * from the token with the stored sub claim associated with the given username.
   *
   * @param request The HttpServletRequest containing the Authorization header with the JWT.
   * @param username The username of the user whose authorization is to be validated.
   * @return true if the sub claim from the token matches the stored sub claim for the username;
   *     false otherwise.
   */
  public boolean validateUserAuthorization(HttpServletRequest request, String username) {
    String tokenSub = getSubFromAccessToken(request);
    String storedTokenSub = userService.getUserFromSubId(username);
    return tokenSub != null && tokenSub.equals(storedTokenSub);
  }

  /**
   * Extracts the sub claim from the JWT contained in the Authorization header of the provided
   * HttpServletRequest.
   *
   * @param request The HttpServletRequest containing the Authorization header.
   * @return The sub claim from the JWT if the token is valid; an error message if the token is
   *     invalid or not found.
   */
  public String getSubFromAccessToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String token = authorizationHeader.substring(7);
      try {
        return extractSub(token);
      } catch (Exception e) {
        return "Error: " + e.getMessage();
      }
    } else {
      return "No Authorization token found";
    }
  }

  /**
   * Decodes the JWT and extracts the sub claim.
   *
   * @param accessToken The JWT from which the sub claim is to be extracted.
   * @return The sub claim if successfully extracted; an error message if an error occurs during
   *     decoding.
   * @throws Exception if JWT decoding fails, encapsulating the cause of the failure.
   */
  private String extractSub(String accessToken) {
    try {
      Jwt jwt = jwtDecoder.decode(accessToken);
      return jwt.getClaimAsString("sub");
    } catch (Exception e) {
      // Log or handle the exception as required
      return "Error decoding JWT: " + e.getMessage();
    }
  }
}
