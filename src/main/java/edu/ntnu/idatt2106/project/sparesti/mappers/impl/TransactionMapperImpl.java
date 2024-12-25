package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Mapper interface for converting between TransactionEntity and
 * TransactionDto objects. This class uses ModelMapper to efficiently map attributes between
 * Transaction DTOs and entities.
 */
@Component
public class TransactionMapperImpl implements Mapper<TransactionEntity, TransactionDto> {
  private ModelMapper modelMapper;

  /**
   * Constructs a new TransactionMapperImpl with a specified ModelMapper. Used for dependency
   * injection
   *
   * @param modelMapper The ModelMapper to use for mapping between object types.
   */
  public TransactionMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Converts a TransactionEntity to a TransactionDto.
   *
   * @param transactionEntity The Transaction entity to be converted to DTO.
   * @return The corresponding TransactionDto.
   */
  @Override
  public TransactionDto mapTo(TransactionEntity transactionEntity) {
    return modelMapper.map(transactionEntity, TransactionDto.class);
  }

  /**
   * Converts a TransactionDto back to a TransactionEntity.
   *
   * @param transactionDto The Transaction DTO to be converted back to an entity.
   * @return The corresponding TransactionEntity.
   */
  @Override
  public TransactionEntity mapFrom(TransactionDto transactionDto) {
    return modelMapper.map(transactionDto, TransactionEntity.class);
  }
}
