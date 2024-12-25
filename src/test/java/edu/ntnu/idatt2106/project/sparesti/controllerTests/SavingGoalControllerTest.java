package edu.ntnu.idatt2106.project.sparesti.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoDetailsResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoGeneralResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoPost;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.SavingGoalService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

/** Test class for SavingGoalController. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SavingGoalControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SavingGoalService savingGoalService;

  @Autowired private ObjectMapper objectMapper;
  @MockBean private UserInfoFromTokenService userInfoFromTokenService;

  @BeforeEach
  void setup() {
    // Mock authorization to always return true for all tests
    when(userInfoFromTokenService.validateUserAuthorization(
            any(HttpServletRequest.class), anyString()))
        .thenReturn(true);
  }

  /** Tests the createSavingGoal endpoint for successfully creating a saving goal. */
  @Test
  public void createSavingGoal() throws Exception {
    SavingGoalDtoPost postDto = new SavingGoalDtoPost();
    SavingGoalDtoGeneralResponse responseDto = new SavingGoalDtoGeneralResponse();

    when(savingGoalService.createSavingGoal(any(SavingGoalDtoPost.class), any(String.class)))
        .thenReturn(responseDto);

    mockMvc
        .perform(
            post("/api/secure/users/johndoe/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
        .andExpect(status().isCreated());
  }

  /** Tests the editSavingGoal endpoint for successfully editing an existing saving goal. */
  @Test
  public void editSavingGoal() throws Exception {
    SavingGoalDtoPost editDto = new SavingGoalDtoPost();
    SavingGoalDtoGeneralResponse editedResponse = new SavingGoalDtoGeneralResponse();

    when(savingGoalService.editSavingGoal(
            any(SavingGoalDtoPost.class), any(String.class), anyLong()))
        .thenReturn(editedResponse);

    mockMvc
        .perform(
            patch("/api/secure/users/johndoe/goals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDto)))
        .andExpect(status().isOk());
  }

  /** Tests the getAllSavingGoals endpoint for retrieving all saving goals for a specific user. */
  @Test
  public void getAllSavingGoals() throws Exception {
    List<SavingGoalDtoGeneralResponse> goalsList = new ArrayList<>();

    when(savingGoalService.getAllSavingGoalsForUser(any(String.class))).thenReturn(goalsList);

    mockMvc.perform(get("/api/secure/users/johndoe/goals")).andExpect(status().isOk());
  }

  /** Tests the getSavingGoal endpoint for retrieving details of a specific saving goal. */
  @Test
  public void getSavingGoal() throws Exception {
    SavingGoalDtoDetailsResponse goalDetails = new SavingGoalDtoDetailsResponse();

    when(savingGoalService.getSavingGoal(anyLong())).thenReturn(goalDetails);

    mockMvc.perform(get("/api/secure/users/johndoe/goals/1")).andExpect(status().isOk());
  }
}
