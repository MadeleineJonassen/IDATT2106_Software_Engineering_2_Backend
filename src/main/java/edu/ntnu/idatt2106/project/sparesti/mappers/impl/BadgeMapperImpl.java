package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.BadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/** Mapper responsible for mapping between Badge entities and badge dtos. */
@Component
public class BadgeMapperImpl implements Mapper<BadgeEntity, BadgeDto> {
  private ModelMapper modelMapper;

  public BadgeMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Takes in an object of Class A and maps it to Class B.
   *
   * @param badgeEntity Object to convert.
   * @return Converted object.
   */
  @Override
  public BadgeDto mapTo(BadgeEntity badgeEntity) {
    return modelMapper.map(badgeEntity, BadgeDto.class);
  }

  /**
   * Takes in an object of Class A and maps it to Class B.
   *
   * @param badgeDto Object to convert.
   * @return Converted object.
   */
  @Override
  public BadgeEntity mapFrom(BadgeDto badgeDto) {
    return null;
  }
}
