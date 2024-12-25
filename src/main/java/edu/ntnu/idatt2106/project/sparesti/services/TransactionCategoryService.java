package edu.ntnu.idatt2106.project.sparesti.services;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import java.util.List;

/** Services interface for transaction category service. */
public interface TransactionCategoryService {

  /**
   * Get all transaction categories.
   *
   * @return a list of every transaction category as a dto.
   */
  List<TransactionCategoryDto> findAllTransactionCategories();
}
