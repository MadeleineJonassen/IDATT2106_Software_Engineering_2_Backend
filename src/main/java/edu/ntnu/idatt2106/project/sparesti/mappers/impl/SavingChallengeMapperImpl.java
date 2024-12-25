package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savingchallenges.SavingChallengeResponseDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingChallengeEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/** Mapper responsible for mapping between SavingChallengeDtos and SavingChallengeEntities. */
@Component
@Log
public class SavingChallengeMapperImpl
    implements Mapper<SavingChallengeEntity, SavingChallengeResponseDto> {

  private final ModelMapper modelMapper;

  public SavingChallengeMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Takes in an object of Class A and maps it to Class B.
   *
   * @param savingChallengeEntity Object to convert.
   * @return Converted object.
   */
  @Override
  public SavingChallengeResponseDto mapTo(SavingChallengeEntity savingChallengeEntity) {
    return modelMapper.map(savingChallengeEntity, SavingChallengeResponseDto.class);
  }

  /**
   * Takes in an object of Class SavingChallengeResponseDto and maps it to Class
   * SavingChallengeEntity.
   *
   * @param savingChallengeResponseDto Object to convert.
   * @return Converted object.
   */
  @Override
  public SavingChallengeEntity mapFrom(SavingChallengeResponseDto savingChallengeResponseDto) {
    return null;
  }
}
