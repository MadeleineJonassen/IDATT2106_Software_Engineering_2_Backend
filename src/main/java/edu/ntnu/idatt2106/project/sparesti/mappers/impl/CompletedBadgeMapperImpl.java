package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.badges.CompletedBadgeDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.CompletedBadgeEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/** Mapper used for mapping between completed badge entities and dtos. */
@Component
public class CompletedBadgeMapperImpl implements Mapper<CompletedBadgeEntity, CompletedBadgeDto> {
  private ModelMapper modelMapper;

  public CompletedBadgeMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Takes in an object of Class A and maps it to Class B.
   *
   * @param completedBadge Object to convert.
   * @return Converted object.
   */
  @Override
  public CompletedBadgeDto mapTo(CompletedBadgeEntity completedBadge) {
    return modelMapper.map(completedBadge, CompletedBadgeDto.class);
  }

  /**
   * Takes in an object of Class B and maps it to Class A.
   *
   * @param completedBadgeDto Object to convert.
   * @return Converted object.
   */
  @Override
  public CompletedBadgeEntity mapFrom(CompletedBadgeDto completedBadgeDto) {
    return null;
  }
}
