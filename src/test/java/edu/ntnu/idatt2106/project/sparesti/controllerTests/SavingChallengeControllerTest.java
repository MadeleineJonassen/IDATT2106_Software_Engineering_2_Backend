package edu.ntnu.idatt2106.project.sparesti.controllerTests;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeResponseDto;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.SavingChallengeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SavingChallengeControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockBean private SavingChallengeService savingChallengeService;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private UserInfoFromTokenService userInfoFromTokenService;

  @BeforeEach
  void setup() {
    // Mock authorization to always return true for all tests
    when(userInfoFromTokenService.validateUserAuthorization(
            any(HttpServletRequest.class), anyString()))
        .thenReturn(true);
  }

  @Test
  void testGetAllChallengesFromGoalWithValidGoalId() throws Exception {

    TransactionCategoryDto mockedTransactionDto1 = new TransactionCategoryDto(1L, "test1");
    TransactionCategoryDto mockedTransactionDto2 = new TransactionCategoryDto(2L, "test2");

    SavingChallengeResponseDto mockedChallengeDto1 = new SavingChallengeResponseDto();
    mockedChallengeDto1.setId(1L);
    mockedChallengeDto1.setTransactionCategory(mockedTransactionDto1);
    SavingChallengeResponseDto mockedChallengeDto2 = new SavingChallengeResponseDto();
    mockedChallengeDto2.setId(2L);
    mockedChallengeDto2.setTransactionCategory(mockedTransactionDto2);

    List<SavingChallengeResponseDto> mockedSavingChallengeResponseDtos =
        Arrays.asList(mockedChallengeDto1, mockedChallengeDto2);

    // Stubbing the service method
    given(savingChallengeService.getAndProcessAllChallengesInGoal(1L))
        .willReturn(mockedSavingChallengeResponseDtos);

    // Perform the GET request and verify the response
    mockMvc
        .perform(get("/api/secure/users/1/goals/1/challenges"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].transactionCategory.id").value(1))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].transactionCategory.id").value(2));
  }

  @Test
  void createSavingChallengeValidInputReturnsCreated() throws Exception {
    // Mock request data
    SavingChallengeRequestDto requestDto = new SavingChallengeRequestDto();
    // Mock response data
    SavingChallengeResponseDto responseDto = new SavingChallengeResponseDto();
    responseDto.setId(1L);

    // Mock service method to return the responseDto
    given(
            savingChallengeService.createSavingChallenge(
                any(SavingChallengeRequestDto.class), eq(1L)))
        .willReturn(responseDto);

    // Perform POST request
    mockMvc
        .perform(
            post("/api/secure/users/1/goals/1/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated());

    // Verify that service method was called with correct parameters
    verify(savingChallengeService).createSavingChallenge(requestDto, 1L);
  }

  @Test
  void getEverySavingChallengeFromUserEntity() throws Exception {
    // Asserts test response
    List<SavingChallengeResponseDto> goalsList = new ArrayList<>();

    // Mock service to return list above
    when(savingChallengeService.getAndProcessUserChallenges(any(String.class)))
        .thenReturn(goalsList);

    // Test if the endpoint response is ok
    mockMvc.perform(get("/api/secure/users/johndoe/challenges")).andExpect(status().isOk());
  }

  @Test
  void finishSavingChallenge() throws Exception {
    when(savingChallengeService.finishChallenge(anyLong(), anyString()))
        .thenReturn(new SavingChallengeResponseDto());

    mockMvc.perform(post("/api/secure/users/johndoe/1/transfer")).andExpect(status().isOk());
  }

  @Test
  void getSuggestions() throws Exception {
    List<SavingChallengeResponseDto> dtos =
        new ArrayList<>(
            Arrays.asList(
                SavingChallengeResponseDto.builder()
                    .transactionCategory(TransactionCategoryDto.builder().id(1L).build())
                    .build(),
                SavingChallengeResponseDto.builder()
                    .transactionCategory(TransactionCategoryDto.builder().id(2L).build())
                    .build(),
                SavingChallengeResponseDto.builder()
                    .transactionCategory(TransactionCategoryDto.builder().id(3L).build())
                    .build()));

    when(savingChallengeService.getChallengeSuggestions(anyLong())).thenReturn(dtos);

    mockMvc
        .perform(get("/api/secure/users/johndoe/goals/1/suggestions"))
        .andExpect(status().isOk());
  }
}
