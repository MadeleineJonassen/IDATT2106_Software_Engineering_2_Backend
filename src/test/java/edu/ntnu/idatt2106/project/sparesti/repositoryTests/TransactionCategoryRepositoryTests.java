package edu.ntnu.idatt2106.project.sparesti.repositoryTests;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionCategoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/** Tests the custom query methods in TransactionCategoryRepository. */
@DataJpaTest
@ActiveProfiles("test")
public class TransactionCategoryRepositoryTests {
  @Autowired TransactionCategoryRepository transactionCategoryRepository;

  private TransactionCategoryEntity transactionCategoryEntity1;
  private TransactionCategoryEntity transactionCategoryEntity2;

  /** Adds test data to the test database before each test. */
  @BeforeEach
  public void setup() {
    transactionCategoryEntity1 = TransactionCategoryEntity.builder().build();
    transactionCategoryEntity2 = TransactionCategoryEntity.builder().build();
    transactionCategoryRepository.save(transactionCategoryEntity1);
    transactionCategoryRepository.save(transactionCategoryEntity2);
  }

  /** Tests that findByIdNotIn can find the one valid transactionCategory. */
  @Test
  public void findByIdNotIn() {
    List<TransactionCategoryEntity> transactionCategoryEntitiesFound =
        transactionCategoryRepository.findByIdNotIn(List.of(transactionCategoryEntity1.getId()));

    assertFalse(transactionCategoryEntitiesFound.isEmpty());
    assertEquals(transactionCategoryEntity2, transactionCategoryEntitiesFound.get(0));
  }

  /**
   * Tests that findByIdNotIn returns an empty list when there are noe valid transactionCategories.
   */
  @Test
  public void findByIdNotInReturnsNull() {
    List<TransactionCategoryEntity> transactionCategoryEntitiesFound =
        transactionCategoryRepository.findByIdNotIn(
            List.of(transactionCategoryEntity1.getId(), transactionCategoryEntity2.getId()));

    assertTrue(transactionCategoryEntitiesFound.isEmpty());
  }

  /**
   * Tests that findByIdNotIn returns an empty list when there are noe valid transactionCategories.
   */
  @Test
  public void findByIdWithNull() {
    List<Long> ids = new ArrayList<>();

    List<TransactionCategoryEntity> transactionCategoryEntitiesFound =
        transactionCategoryRepository.findByIdNotIn(ids);

    assertTrue(transactionCategoryEntitiesFound.isEmpty());
  }
}
