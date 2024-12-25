package edu.ntnu.idatt2106.project.sparesti.controllerTests;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.TransactionCategoryDto;
import edu.ntnu.idatt2106.project.sparesti.services.TransactionCategoryService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionCategoryControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockBean private TransactionCategoryService transactionCategoryService;

  @Test
  void getEverySavingChallengeFromUserEntity() throws Exception {
    // Asserts test response
    List<TransactionCategoryDto> transactionCategoryList = new ArrayList<>();

    // Mock service to return list above
    when(transactionCategoryService.findAllTransactionCategories())
        .thenReturn(transactionCategoryList);

    // Test if the endpoint response is ok
    mockMvc.perform(get("/api/secure/transactions/categories")).andExpect(status().isOk());
  }
}
