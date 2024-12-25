package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransferDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransferEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Mapper interface for converting between TransferEntity and TransferDto
 * objects. This class uses ModelMapper to efficiently map attributes between Transfer DTOs and
 * entities.
 */
@Component
public class TransferMapperImpl implements Mapper<TransferEntity, TransferDto> {
  private final ModelMapper modelMapper;

  /**
   * Constructs a new TransferMapperImpl with a specified ModelMapper. Used for dependency injection
   *
   * @param modelMapper The ModelMapper to use for object property mapping.
   */
  public TransferMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Converts a TransferEntity instance to a TransferDto instance.
   *
   * @param transferEntity The Transfer entity to convert to a DTO.
   * @return The corresponding TransferDto object.
   */
  @Override
  public TransferDto mapTo(TransferEntity transferEntity) {
    return modelMapper.map(transferEntity, TransferDto.class);
  }

  /**
   * Converts a TransferDto instance back to a TransferEntity instance.
   *
   * @param transferDto The Transfer DTO to convert back to an entity.
   * @return The corresponding TransferEntity object.
   */
  @Override
  public TransferEntity mapFrom(TransferDto transferDto) {
    return modelMapper.map(transferDto, TransferEntity.class);
  }
}
