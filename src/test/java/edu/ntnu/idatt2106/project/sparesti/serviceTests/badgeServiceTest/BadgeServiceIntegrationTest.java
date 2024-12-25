package edu.ntnu.idatt2106.project.sparesti.serviceTests.badgeServiceTest;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeProgressDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeTierDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.CompletedBadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.*;
import edu.ntnu.idatt2106.project.sparesti.repositories.BadgeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.BadgeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BadgeServiceIntegrationTest {
  @Autowired private BadgeService badgeService;

  @Autowired private BadgeRepository badgeRepository;

  @Autowired private UserRepository userRepository;

  @BeforeEach
  public void setup() {
    BadgeEntity badge1 = BadgeEntity.builder().name("Griseflink Sparer").build();

    BadgeTierEntity badgeTierEntity1A =
        BadgeTierEntity.builder()
            .target(1)
            .description("Description 1")
            .tier(1)
            .badge(badge1)
            .build();

    BadgeTierEntity badgeTierEntity1B =
        BadgeTierEntity.builder()
            .target(5)
            .description("Description 2")
            .tier(2)
            .badge(badge1)
            .build();

    BadgeTierEntity badgeTierEntity1C =
        BadgeTierEntity.builder()
            .target(20)
            .description("Description 3")
            .tier(3)
            .badge(badge1)
            .build();

    badge1.setBadgeTiers(Arrays.asList(badgeTierEntity1A, badgeTierEntity1B, badgeTierEntity1C));

    BadgeEntity badge2 = BadgeEntity.builder().name("Sus-pig-us").build();

    BadgeTierEntity badgeTierEntity2A =
        BadgeTierEntity.builder()
            .target(1)
            .description("Description 1")
            .tier(1)
            .badge(badge2)
            .build();

    BadgeTierEntity badgeTierEntity2B =
        BadgeTierEntity.builder()
            .target(3)
            .description("Description 2")
            .tier(2)
            .badge(badge2)
            .build();

    BadgeTierEntity badgeTierEntity2C =
        BadgeTierEntity.builder()
            .target(10)
            .description("Description 3")
            .tier(3)
            .badge(badge2)
            .build();

    badge2.setBadgeTiers(Arrays.asList(badgeTierEntity2A, badgeTierEntity2B, badgeTierEntity2C));

    CompletedBadgeEntity completedBadge1 =
        CompletedBadgeEntity.builder().name("Griseflink Sparer").tier(1).build();

    CompletedBadgeEntity completedBadge2 =
        CompletedBadgeEntity.builder().name("Griseflink Sparer").tier(2).build();

    CompletedBadgeEntity completedBadge3 =
        CompletedBadgeEntity.builder().name("Sus-pig-us").tier(1).build();

    List<CompletedBadgeEntity> completedBadges =
        Arrays.asList(completedBadge1, completedBadge2, completedBadge3);

    UserEntity userEntity =
        UserEntity.builder()
            .username("User")
            .email("email@email.com")
            .fullName("User name")
            .build();

    BadgeProgressEntity badgeProgressEntity1 =
        BadgeProgressEntity.builder()
            .badgeEntity(badge1)
            .userEntity(userEntity)
            .progress(2)
            .build();

    BadgeProgressEntity badgeProgressEntity2 =
        BadgeProgressEntity.builder()
            .badgeEntity(badge2)
            .userEntity(userEntity)
            .progress(4)
            .build();

    userEntity.setBadgeProgress(Arrays.asList(badgeProgressEntity1, badgeProgressEntity2));
    userEntity.setCompletedBadges(completedBadges);

    badgeRepository.saveAll(Arrays.asList(badge1, badge2));
    userRepository.save(userEntity);
  }

  @AfterEach
  public void cleanup() {
    userRepository.deleteAll();
  }

  @Test
  void testCanGetAllBadges() {
    List<BadgeDto> badges = badgeService.getAllBadges();
    assertEquals(2, badges.size());
    assertNotEquals(3, badges.size());

    assertEquals("Griseflink Sparer", badges.get(0).getName());
    assertEquals(1, badges.get(0).getBadgeTiers().get(0).getTarget());
    assertEquals(5, badges.get(0).getBadgeTiers().get(1).getTarget());
    assertEquals(20, badges.get(0).getBadgeTiers().get(2).getTarget());

    assertEquals(1, badges.get(0).getBadgeTiers().get(0).getTier());
    assertEquals(2, badges.get(0).getBadgeTiers().get(1).getTier());
    assertEquals(3, badges.get(0).getBadgeTiers().get(2).getTier());

    assertEquals("Description 1", badges.get(0).getBadgeTiers().get(0).getDescription());
    assertEquals("Description 2", badges.get(0).getBadgeTiers().get(1).getDescription());
    assertEquals("Description 3", badges.get(0).getBadgeTiers().get(2).getDescription());

    assertEquals("Sus-pig-us", badges.get(1).getName());
    assertEquals(1, badges.get(1).getBadgeTiers().get(0).getTarget());
    assertEquals(3, badges.get(1).getBadgeTiers().get(1).getTarget());
    assertEquals(10, badges.get(1).getBadgeTiers().get(2).getTarget());

    assertEquals(1, badges.get(1).getBadgeTiers().get(0).getTier());
    assertEquals(2, badges.get(1).getBadgeTiers().get(1).getTier());
    assertEquals(3, badges.get(1).getBadgeTiers().get(2).getTier());

    assertEquals("Description 1", badges.get(1).getBadgeTiers().get(0).getDescription());
    assertEquals("Description 2", badges.get(1).getBadgeTiers().get(1).getDescription());
    assertEquals("Description 3", badges.get(1).getBadgeTiers().get(2).getDescription());
  }

  @Test
  void testCanGetAllBadgesOnUser() {
    List<CompletedBadgeDto> badgesForUser = badgeService.getBadgesByUser("User");

    assertEquals(3, badgesForUser.size());

    assertEquals("Griseflink Sparer", badgesForUser.get(0).getName());
    assertEquals(1, badgesForUser.get(0).getTier());

    assertEquals("Griseflink Sparer", badgesForUser.get(1).getName());
    assertEquals(2, badgesForUser.get(1).getTier());

    assertEquals("Sus-pig-us", badgesForUser.get(2).getName());
    assertEquals(1, badgesForUser.get(2).getTier());
  }

  @Test
  void testCanGetUserProgressOnBadge() {
    List<BadgeProgressDto> badgeProgress = badgeService.getAllBadgeProgress("User");

    assertEquals(2, badgeProgress.size());
    assertEquals(2, badgeProgress.get(0).getProgress());
    assertEquals("Griseflink Sparer", badgeProgress.get(0).getBadgeDto().getName());

    assertEquals(4, badgeProgress.get(1).getProgress());
    assertEquals("Sus-pig-us", badgeProgress.get(1).getBadgeDto().getName());
  }
}
