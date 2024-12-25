package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.BankAccountDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Mapper interface for converting between BankAccountEntity and
 * BankAccountDto objects. This class uses ModelMapper to map properties between DTO (Data Transfer
 * Object) and entity classes.
 */
@Component
public class BankAccountMapperImpl implements Mapper<BankAccountEntity, BankAccountDto> {
  private final ModelMapper modelMapper;

  /**
   * Constructs a new BankAccountMapperImpl with the specified ModelMapper. Used for dependency
   * injection
   *
   * @param modelMapper The ModelMapper to use for mapping between object types.
   */
  public BankAccountMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Converts a BankAccountEntity instance to a BankAccountDto instance.
   *
   * @param bankAccountEntity The bank account entity to convert to DTO.
   * @return The corresponding BankAccountDto object.
   */
  @Override
  public BankAccountDto mapTo(BankAccountEntity bankAccountEntity) {
    return modelMapper.map(bankAccountEntity, BankAccountDto.class);
  }

  /**
   * Converts a BankAccountDto instance back to a BankAccountEntity instance.
   *
   * @param bankAccountDto The bank account DTO to convert back to an entity.
   * @return The corresponding BankAccountEntity object.
   */
  @Override
  public BankAccountEntity mapFrom(BankAccountDto bankAccountDto) {
    return modelMapper.map(bankAccountDto, BankAccountEntity.class);
  }
}
