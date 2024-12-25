package edu.ntnu.idatt2106.project.sparesti.controllers;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import edu.ntnu.idatt2106.project.sparesti.services.TransactionCategoryService;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing transaction categories. This controller handles HTTP requests
 * related to transaction categories, allowing clients to retrieve a list of all transaction
 * categories.
 */
@RestController
public class TransactionCategoryController {

  private TransactionCategoryService transactionCategoryService;

  /**
   * Constructs a new TransactionCategoryController with a dependency on TransactionCategoryService.
   *
   * @param transactionCategoryService the service that provides transaction category operations
   */
  public TransactionCategoryController(TransactionCategoryService transactionCategoryService) {
    this.transactionCategoryService = transactionCategoryService;
  }

  /**
   * Retrieves all transaction categories as a list of DTOs. This method handles the GET request at
   * the specified URL and uses the TransactionCategoryService to retrieve and return all
   * transaction categories.
   *
   * @return a list of {@link TransactionCategoryDto} representing all transaction categories
   */
  @CrossOrigin("http://localhost:5173")
  @GetMapping("api/secure/transactions/categories")
  public List<TransactionCategoryDto> getAllCategories() {
    return transactionCategoryService.findAllTransactionCategories();
  }
}
