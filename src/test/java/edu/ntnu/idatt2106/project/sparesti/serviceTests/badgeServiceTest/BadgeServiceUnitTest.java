package edu.ntnu.idatt2106.project.sparesti.serviceTests.badgeServiceTest;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.impl.BadgeMapperImpl;
import edu.ntnu.idatt2106.project.sparesti.repositories.BadgeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.BadgeService;
import edu.ntnu.idatt2106.project.sparesti.services.impl.BadgeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BadgeServiceUnitTest {
  @Mock private UserRepository userRepository;

  @Mock private BadgeMapperImpl badgeMapper;

  @Mock private BadgeRepository badgeRepository;

  @InjectMocks private BadgeServiceImpl badgeService;

  /*
  @Test
  void testGetAllBadges() {
    BadgeEntity badge1 = BadgeEntity.builder().id(1L).name("Sus-Pig-Us").tier(3).build();

    BadgeEntity badge2 = BadgeEntity.builder().id(2L).name("Peppa").tier(2).build();

    BadgeEntity badge3 = BadgeEntity.builder().id(3L).name("Bacon").tier(1).build();

    List<BadgeEntity> badges = Arrays.asList(badge1, badge2, badge3);

    when(badgeRepository.findAll()).thenReturn(badges);

    List<BadgeDto> result = badgeService.getAllBadges();

    System.out.println(result);
    assertEquals(3, result.size());
    // assertEquals("Sus-Pig-Us", result.get(1).getName());
  }*/
}
