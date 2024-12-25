package edu.ntnu.idatt2106.project.sparesti.services.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionCategoryRepository;
import edu.ntnu.idatt2106.project.sparesti.services.TransactionCategoryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing transaction categories. This service handles the operations
 * related to retrieving transaction category data and transforming it to data transfer objects
 * (DTOs) for use in higher-level application layers.
 */
@Service
@Log
public class TransactionCategoryServiceImpl implements TransactionCategoryService {

  private final TransactionCategoryRepository transactionCategoryRepository;
  private final Mapper<TransactionCategoryEntity, TransactionCategoryDto> transactionCategoryMapper;

  /**
   * Constructs a new TransactionCategoryServiceImpl with necessary dependencies.
   *
   * @param transactionCategoryRepository the repository used for accessing transaction category
   *     data
   * @param transactionCategoryMapper the mapper used for converting transaction category entities
   *     to DTOs
   */
  public TransactionCategoryServiceImpl(
      TransactionCategoryRepository transactionCategoryRepository,
      Mapper<TransactionCategoryEntity, TransactionCategoryDto> transactionCategoryMapper) {
    this.transactionCategoryRepository = transactionCategoryRepository;
    this.transactionCategoryMapper = transactionCategoryMapper;
  }

  /**
   * Retrieves all transaction categories and maps them to DTOs. This method fetches all transaction
   * category entities using the transaction category repository and converts each entity to a DTO
   * using the configured mapper.
   *
   * @return a list of {@link TransactionCategoryDto} representing all transaction categories
   */
  @Override
  public List<TransactionCategoryDto> findAllTransactionCategories() {
    List<TransactionCategoryEntity> categoryEntities = transactionCategoryRepository.findAll();
    return categoryEntities.stream()
        .map(transactionCategoryMapper::mapTo)
        .collect(Collectors.toList());
  }
}
