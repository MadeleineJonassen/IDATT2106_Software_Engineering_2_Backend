package edu.ntnu.idatt2106.project.sparesti.serviceTests;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.TransactionCategoryEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.impl.TransactionCategoryMapperImpl;
import edu.ntnu.idatt2106.project.sparesti.repositories.TransactionCategoryRepository;
import edu.ntnu.idatt2106.project.sparesti.services.impl.TransactionCategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link TransactionCategoryServiceImpl}. This class contains tests to verify the
 * functionality of methods in the TransactionCategoryServiceImpl, particularly focusing on the
 * retrieval and mapping of transaction category data.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionCategoryServiceTest {

  @MockBean private TransactionCategoryRepository transactionCategoryRepository;

  @MockBean private TransactionCategoryMapperImpl transactionCategoryMapper;

  @Autowired private TransactionCategoryServiceImpl transactionCategoryService;

  /**
   * Test for {@link TransactionCategoryServiceImpl#findAllTransactionCategories()}. This test
   * verifies that the findAllTransactionCategories method correctly retrieves transaction category
   * entities using the transactionCategoryRepository, maps them to DTOs using
   * transactionCategoryMapper, and returns the correct list of DTOs. The test checks that the
   * correct methods on mock beans are called and that the result matches expected values.
   */
  @Test
  void findAllTransactionCategoriesTest() {
    TransactionCategoryEntity testCategory1 = new TransactionCategoryEntity();
    testCategory1.setId(1L);
    testCategory1.setName("Dagligvarer");

    TransactionCategoryEntity testCategory2 = new TransactionCategoryEntity();
    testCategory2.setId(2L);
    testCategory2.setName("Faste utgifter");

    List<TransactionCategoryEntity> mockEntities = Arrays.asList(testCategory1, testCategory2);
    given(transactionCategoryRepository.findAll()).willReturn(mockEntities);

    TransactionCategoryDto testCategoryDto1 = new TransactionCategoryDto();
    testCategoryDto1.setId(1L);
    testCategoryDto1.setName("Dagligvarer");

    TransactionCategoryDto testCategoryDto2 = new TransactionCategoryDto();
    testCategoryDto2.setId(2L);
    testCategoryDto2.setName("Faste utgifter");

    given(transactionCategoryMapper.mapTo(testCategory1)).willReturn(testCategoryDto1);
    given(transactionCategoryMapper.mapTo(testCategory2)).willReturn(testCategoryDto2);

    List<TransactionCategoryDto> resultTestCategoryDtos =
        transactionCategoryService.findAllTransactionCategories();

    verify(transactionCategoryRepository).findAll();
    verify(transactionCategoryMapper).mapTo(testCategory1);
    verify(transactionCategoryMapper).mapTo(testCategory2);

    assertEquals(2, resultTestCategoryDtos.size());
    assertEquals(testCategoryDto1, resultTestCategoryDtos.get(0));
    assertEquals(testCategoryDto2, resultTestCategoryDtos.get(1));
  }
}
