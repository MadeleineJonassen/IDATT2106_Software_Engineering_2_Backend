package edu.ntnu.idatt2106.project.sparesti.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetails;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetailsRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDto;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  @Autowired private ObjectMapper objectMapper;
  @MockBean private UserInfoFromTokenService userInfoFromTokenService;

  @BeforeEach
  void setup() {
    // Mock authorization to always return true for all tests
    when(userInfoFromTokenService.validateUserAuthorization(
            any(HttpServletRequest.class), anyString()))
        .thenReturn(true);
  }

  /**
   * Sjekker at en bruker blir lagret i databasen med riktig user informasjon.
   *
   * @throws Exception hvis objectMapper ikke kan tolke og skrive userDto som tekst.
   */
  @Test
  void saveUserWithUnusedEmailTest() throws Exception {
    // Lager et userDto som skal sendes til endepunktet
    // UserDto userDto = new UserDto(1L, "testUsername", "testMail@mail.com", "testName");
    UserDto userDto =
        UserDto.builder()
            .id(1L)
            .username("testUsername")
            .email("testMail@mail.com")
            .fullName("testName")
            .build();

    // Sier at user service sin createUser metode skal retunere userDto som er laget over
    given(userService.createUserWithDefaultBankAccounts(any(UserDto.class))).willReturn(userDto);

    // Mocker post-requesten, der JSON svaret man får som inneholder userDTO-en skal bli gjort om
    // til en
    // string, deretter sjekkes det om brukernavnet i svaret er det samme som i userDto som ble
    // definert i starten
    mockMvc
        .perform(
            post("/api/secure/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testUsername"));
  }

  /**
   * Sjekker at en respons med statuskode 4xx gis når UserDto gis med en epost som allerede er
   * registrert.
   *
   * @throws Exception hvis objectMapper ikke kan tolke og skrive userDto som tekst.
   */
  @Test
  void saveUserWithUsedEmailTest() throws Exception {
    // UserDto userDto = new UserDto(1L, "username", "user@mail.com", "fullName");
    UserDto userDto =
        UserDto.builder()
            .id(1L)
            .username("username")
            .email("user@mail.com")
            .fullName("fullName")
            .build();

    // Mocker at userService kaster UserAlreadyExistsExceptions (når oppgitt epost allerede
    // er registrert i db).
    given(userService.createUserWithDefaultBankAccounts(any(UserDto.class)))
        .willThrow(ResponseStatusException.class);

    // Mocker post-request for å registrere bruker
    mockMvc
        .perform(
            post("/api/secure/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().is5xxServerError());
  }

  /**
   * Tests the RESTful endpoint for retrieving all bank accounts associated with a specific user.
   *
   * @throws Exception if there's an error performing the mock HTTP request or an issue within the
   *     testing framework.
   */
  @Test
  void getAllBankAccountsByUser() throws Exception {
    String username = "john_doe";
    List<BankAccountDto> accounts =
        Arrays.asList(
            new BankAccountDto(1L, 100L, "John's Savings Account", 2500.00),
            new BankAccountDto(2L, 100L, "John's Checking Account", 1500.00));

    when(userService.getAllBankAccounts(username)).thenReturn(accounts);

    mockMvc
        .perform(
            get("/api/secure/users/{username}/accounts", username)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].userEntityId", is(100)))
        .andExpect(jsonPath("$[0].name", is("John's Savings Account")))
        .andExpect(jsonPath("$[0].sum", is(2500.00)))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].userEntityId", is(100)))
        .andExpect(jsonPath("$[1].name", is("John's Checking Account")))
        .andExpect(jsonPath("$[1].sum", is(1500.00)));
  }

  @Test
  void testGetUserDetails() throws Exception {
    BankAccountDto savingsAccount =
        BankAccountDto.builder().userEntityId(1L).sum(50).id(1L).name("Savings").build();

    BankAccountDto checkingAccount =
        BankAccountDto.builder().userEntityId(1L).sum(31).id(2L).name("Checking").build();

    UserDetails userDetails =
        UserDetails.builder()
            .id(1L)
            .imageUrl("image.com")
            .email("email@email.com")
            .username("user")
            .fullName("Test User")
            .preferredSavingsAccount(savingsAccount)
            .preferredCheckingAccount(checkingAccount)
            .build();

    when(userService.getUserDetails("user")).thenReturn(userDetails);

    mockMvc
        .perform(get("/api/secure/users/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.fullName").value("Test User"))
        .andExpect(jsonPath("$.email").value("email@email.com"))
        .andExpect(jsonPath("$.imageUrl").value("image.com"))
        .andExpect(jsonPath("$.preferredSavingsAccount.sum").value(50))
        .andExpect(jsonPath("$.preferredSavingsAccount.id").value(1L))
        .andExpect(jsonPath("$.preferredSavingsAccount.userEntityId").value(1L))
        .andExpect(jsonPath("$.preferredSavingsAccount.name").value("Savings"))
        .andExpect(jsonPath("$.preferredCheckingAccount.sum").value(31))
        .andExpect(jsonPath("$.preferredCheckingAccount.id").value(2L))
        .andExpect(jsonPath("$.preferredCheckingAccount.userEntityId").value(1L))
        .andExpect(jsonPath("$.preferredCheckingAccount.name").value("Checking"));
  }

  @Test
  void testEditUserDetails() throws Exception {
    BankAccountDto savingsAccount =
        BankAccountDto.builder().userEntityId(1L).sum(50).id(1L).name("Savings").build();

    BankAccountDto checkingAccount =
        BankAccountDto.builder().userEntityId(1L).sum(31).id(2L).name("Checking").build();

    UserDetails userDetails =
        UserDetails.builder()
            .id(1L)
            .imageUrl("newimage.com")
            .email("email@email.com")
            .username("user")
            .fullName("Test User")
            .preferredSavingsAccount(savingsAccount)
            .preferredCheckingAccount(checkingAccount)
            .build();

    UserDetailsRequestDto editingData =
        UserDetailsRequestDto.builder().imageUrl("newimage.com").build();

    when(userService.editUser("user", editingData)).thenReturn(userDetails);

    mockMvc
        .perform(
            patch("/api/secure/users/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editingData)))
        .andExpect(status().isOk());
  }
}
