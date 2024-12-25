package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.savinggoals.SavingGoalDtoGeneralResponse;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingGoalEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/** A Mapper responsible for mapping between Saving Goal Entities and DTOs. */
@Component
@Log
public class SavingGoalMapperImpl
    implements Mapper<SavingGoalEntity, SavingGoalDtoGeneralResponse> {

  private ModelMapper modelMapper;

  /**
   * Constructor for the mapper, injecting the modelMapper dependency.
   *
   * @param modelMapper The modelMapper dependency
   */
  public SavingGoalMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Handles mapping from an entity to a dto.
   *
   * @param savingGoalEntity Saving Goal Entity to convert
   * @return Converted Saving Goal Dto
   */
  @Override
  public SavingGoalDtoGeneralResponse mapTo(SavingGoalEntity savingGoalEntity) {
    SavingGoalDtoGeneralResponse savingGoalDtoGeneralResponse =
        modelMapper.map(savingGoalEntity, SavingGoalDtoGeneralResponse.class);
    return savingGoalDtoGeneralResponse;
  }

  /**
   * Handles mapping from a dto to an entity.
   *
   * @param savingGoalDto Saving Goal Dto to convert
   * @return Converted Saving Goal Entity
   */
  @Override
  public SavingGoalEntity mapFrom(SavingGoalDtoGeneralResponse savingGoalDto) {
    return null;
  }
}
