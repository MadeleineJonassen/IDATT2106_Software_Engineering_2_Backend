package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeProgressDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeProgressEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/** Mapper responsible for mapping between badge progress entities and dtos. */
@Component
public class BadgeProgressMapperImpl implements Mapper<BadgeProgressEntity, BadgeProgressDto> {
  private ModelMapper modelMapper;

  public BadgeProgressMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Takes in an object of Class A and maps it to Class B.
   *
   * @param badgeProgressEntity Object to convert.
   * @return Converted object.
   */
  @Override
  public BadgeProgressDto mapTo(BadgeProgressEntity badgeProgressEntity) {
    return modelMapper.map(badgeProgressEntity, BadgeProgressDto.class);
  }

  /**
   * Takes in an object of Class A and maps it to Class B.
   *
   * @param badgeProgressDto Object to convert.
   * @return Converted object.
   */
  @Override
  public BadgeProgressEntity mapFrom(BadgeProgressDto badgeProgressDto) {
    return null;
  }
}
