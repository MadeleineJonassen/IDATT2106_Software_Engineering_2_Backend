package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Mapper interface for transaction categories. Provides functionality to map
 * between TransactionCategoryEntity and TransactionCategoryDto using ModelMapper. This class is
 * intended to abstract and simplify the transformation between entity objects and DTOs, ensuring
 * that the business logic or controllers can operate with the appropriate data formats.
 */
@Component
public class TransactionCategoryMapperImpl
    implements Mapper<TransactionCategoryEntity, TransactionCategoryDto> {

  private final ModelMapper modelMapper;

  /**
   * Constructs a new TransactionCategoryMapperImpl with a given ModelMapper. The ModelMapper is a
   * versatile tool for automating the mapping of properties between objects, making it easy to
   * configure mappings between entity classes and their corresponding DTOs.
   *
   * @param modelMapper the ModelMapper instance used for object mappings
   */
  public TransactionCategoryMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Maps a TransactionCategoryEntity to a TransactionCategoryDto. Uses the ModelMapper to convert
   * the entity to its corresponding DTO format, facilitating the transfer of data between the
   * service layer and the web layer or external applications.
   *
   * @param transactionCategoryEntity the entity to be mapped to DTO
   * @return the mapped TransactionCategoryDto
   */
  @Override
  public TransactionCategoryDto mapTo(TransactionCategoryEntity transactionCategoryEntity) {
    return modelMapper.map(transactionCategoryEntity, TransactionCategoryDto.class);
  }

  /**
   * Maps a TransactionCategoryDto back to a TransactionCategoryEntity. This method is useful for
   * converting DTOs received from API requests into entity format for persistence or further
   * processing in the service layer.
   *
   * @param transactionCategoryDto the DTO to be mapped back to an entity
   * @return the mapped TransactionCategoryEntity
   */
  @Override
  public TransactionCategoryEntity mapFrom(TransactionCategoryDto transactionCategoryDto) {
    return modelMapper.map(transactionCategoryDto, TransactionCategoryEntity.class);
  }
}
