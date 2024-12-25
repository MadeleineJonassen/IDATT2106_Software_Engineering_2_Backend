package edu.ntnu.idatt2106.project.sparesti.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeTierDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.CompletedBadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.security.UserInfoFromTokenService;
import edu.ntnu.idatt2106.project.sparesti.services.BadgeService;
import edu.ntnu.idatt2106.project.sparesti.services.impl.BadgeServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BadgeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private BadgeService badgeService;

  @MockBean private UserInfoFromTokenService userInfoFromTokenService;

  @BeforeEach
  void setup() {
    // Mock authorization to always return true for all tests
    when(userInfoFromTokenService.validateUserAuthorization(
            any(HttpServletRequest.class), anyString()))
        .thenReturn(true);
  }

  @Test
  void testGetAllBadges() throws Exception {
    BadgeTierDto tier1 = BadgeTierDto.builder().target(10).tier(1).id(1L).build();
    BadgeTierDto tier2 = BadgeTierDto.builder().target(50).tier(2).id(2L).build();
    BadgeTierDto tier3 = BadgeTierDto.builder().target(100).tier(3).id(3L).build();

    BadgeTierDto tierb1 = BadgeTierDto.builder().target(2).tier(1).id(1L).build();
    BadgeTierDto tierb2 = BadgeTierDto.builder().target(5).tier(2).id(2L).build();
    BadgeTierDto tierb3 = BadgeTierDto.builder().target(15).tier(3).id(3L).build();

    BadgeDto badge1 =
        BadgeDto.builder()
            .name("Pigus")
            .id(1L)
            .badgeTiers(Arrays.asList(tier1, tier2, tier3))
            .build();

    BadgeDto badge2 =
        BadgeDto.builder()
            .name("Griseflink")
            .id(2L)
            .badgeTiers(Arrays.asList(tierb1, tierb2, tierb3))
            .build();

    List<BadgeDto> badges = Arrays.asList(badge1, badge2);

    given(badgeService.getAllBadges()).willReturn(badges);

    mockMvc
        .perform(get("/api/secure/badges"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].name").value("Pigus"))
        .andExpect(jsonPath("$[0].badgeTiers[0].tier").value(1))
        .andExpect(jsonPath("$[0].badgeTiers[0].target").value(10))
        .andExpect(jsonPath("$[0].badgeTiers[1].tier").value(2))
        .andExpect(jsonPath("$[0].badgeTiers[1].target").value(50))
        .andExpect(jsonPath("$[0].badgeTiers[2].tier").value(3))
        .andExpect(jsonPath("$[0].badgeTiers[2].target").value(100))
        .andExpect(jsonPath("$[1].id").value(2L))
        .andExpect(jsonPath("$[1].name").value("Griseflink"))
        .andExpect(jsonPath("$[1].badgeTiers[0].tier").value(1))
        .andExpect(jsonPath("$[1].badgeTiers[0].target").value(2))
        .andExpect(jsonPath("$[1].badgeTiers[1].tier").value(2))
        .andExpect(jsonPath("$[1].badgeTiers[1].target").value(5))
        .andExpect(jsonPath("$[1].badgeTiers[2].tier").value(3))
        .andExpect(jsonPath("$[1].badgeTiers[2].target").value(15))
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void testGetAllBadgesByUser() throws Exception {
    CompletedBadgeDto completedBadgeDto1 =
        CompletedBadgeDto.builder().tier(1).name("Griseflink Sparer").id(1L).build();

    CompletedBadgeDto completedBadgeDto2 =
        CompletedBadgeDto.builder().tier(1).name("Sus-pig-us").id(2L).build();

    CompletedBadgeDto completedBadgeDto3 =
        CompletedBadgeDto.builder().tier(2).name("Sus-pig-us").id(3L).build();

    List<CompletedBadgeDto> badges =
        Arrays.asList(completedBadgeDto1, completedBadgeDto2, completedBadgeDto3);

    given(badgeService.getBadgesByUser("User")).willReturn(badges);

    mockMvc
        .perform(get("/api/secure/users/User/badges"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].name").value("Griseflink Sparer"))
        .andExpect(jsonPath("$[0].tier").value(1))
        .andExpect(jsonPath("$[1].id").value(2L))
        .andExpect(jsonPath("$[1].name").value("Sus-pig-us"))
        .andExpect(jsonPath("$[1].tier").value(1))
        .andExpect(jsonPath("$[2].id").value(3L))
        .andExpect(jsonPath("$[2].name").value("Sus-pig-us"))
        .andExpect(jsonPath("$[2].tier").value(2))
        .andExpect(jsonPath("$", hasSize(3)));
  }
}
