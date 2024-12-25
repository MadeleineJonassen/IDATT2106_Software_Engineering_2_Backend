package edu.ntnu.idatt2106.project.sparesti.services.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeProgressDto;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.CompletedBadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.CompletedBadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.impl.BadgeMapperImpl;
import edu.ntnu.idatt2106.project.sparesti.mappers.impl.BadgeProgressMapperImpl;
import edu.ntnu.idatt2106.project.sparesti.mappers.impl.CompletedBadgeMapperImpl;
import edu.ntnu.idatt2106.project.sparesti.repositories.BadgeRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.BadgeService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Service responsible for providing operations against badge entities. */
@Service
public class BadgeServiceImpl implements BadgeService {
  private UserRepository userRepository;

  private BadgeRepository badgeRepository;

  private BadgeMapperImpl badgeMapper;

  private BadgeProgressMapperImpl badgeProgressMapper;

  private CompletedBadgeMapperImpl completedBadgeMapper;

  /**
   * Constructor for the badge service.
   *
   * @param userRepository The user repository storing data about users
   * @param badgeMapper The mappers responsible for mapping between badge entities and dtos
   * @param badgeRepository The badge repository storing data about badges
   */
  public BadgeServiceImpl(
      UserRepository userRepository,
      BadgeMapperImpl badgeMapper,
      BadgeRepository badgeRepository,
      BadgeProgressMapperImpl badgeProgressMapper,
      CompletedBadgeMapperImpl completedBadgeMapper) {
    this.userRepository = userRepository;
    this.badgeRepository = badgeRepository;
    this.badgeMapper = badgeMapper;
    this.badgeProgressMapper = badgeProgressMapper;
    this.completedBadgeMapper = completedBadgeMapper;
  }

  /**
   * Service that finds all badges registered on a user.
   *
   * @param username Username of the user to check for
   * @return List of found badges
   */
  @Override
  public List<CompletedBadgeDto> getBadgesByUser(String username) {
    UserEntity userEntity =
        userRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user with that username."));
    return userEntity.getCompletedBadges().stream()
        .map(badgeEntity -> completedBadgeMapper.mapTo(badgeEntity))
        .toList();
  }

  /**
   * Service that finds all badges.
   *
   * @return List of badges
   */
  @Override
  public List<BadgeDto> getAllBadges() {
    return badgeRepository.findAll().stream()
        .map(badgeEntity -> badgeMapper.mapTo(badgeEntity))
        .toList();
  }

  /**
   * Service that finds progress of all badges on user.
   *
   * @return List of badges with progress
   */
  @Override
  public List<BadgeProgressDto> getAllBadgeProgress(String username) {
    UserEntity userEntity =
        userRepository
            .findUserEntityByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user with that username"));
    return userEntity.getBadgeProgress().stream()
        .map(badgeProgressEntity -> badgeProgressMapper.mapTo(badgeProgressEntity))
        .toList();
  }
}
