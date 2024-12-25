package edu.ntnu.idatt2106.project.sparesti.serviceTests.userServiceTest;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetails;
import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDetailsRequestDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.BankAccountEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.repositories.BankAccountRepository;
import edu.ntnu.idatt2106.project.sparesti.repositories.UserRepository;
import edu.ntnu.idatt2106.project.sparesti.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserServiceIntegrationTest {
  @Autowired private UserRepository userRepository;

  @Autowired private UserService userService;

  @Autowired private ModelMapper modelMapper;

  @Autowired private BankAccountRepository bankAccountRepository;

  @BeforeEach
  public void setup() {
    BankAccountEntity savingsAccount =
        BankAccountEntity.builder().sum(31).name("Savings").id(2L).build();

    BankAccountEntity savingsAccount2 =
        BankAccountEntity.builder().sum(3100).name("Savings").id(4L).build();

    BankAccountEntity checkingAccount =
        BankAccountEntity.builder().sum(500).name("Checking").id(1L).build();

    BankAccountEntity checkingAccount2 =
        BankAccountEntity.builder().sum(345).name("Checking").id(3L).build();

    BankAccountEntity checkingAccount3 =
        BankAccountEntity.builder().sum(345).name("Checking").id(5L).build();

    bankAccountRepository.save(savingsAccount2);
    bankAccountRepository.save(checkingAccount2);
    bankAccountRepository.save(checkingAccount3);
    bankAccountRepository.save(savingsAccount);
    bankAccountRepository.save(checkingAccount);

    UserEntity userEntity =
        UserEntity.builder()
            .id(1L)
            .username("username")
            .email("email@email.com")
            .fullName("Test user")
            .imageUrl("image.com")
            .preferredSavingsAccount(savingsAccount)
            .preferredCheckingAccount(checkingAccount)
            .lastCompletedChallengeDate(LocalDate.now())
            .build();

    userEntity.setBankAccounts(
        Arrays.asList(savingsAccount, savingsAccount2, checkingAccount, checkingAccount2));

    userRepository.save(userEntity);
  }

  @Test
  void testCanGetUserDetails() {
    UserDetails foundUser = userService.getUserDetails("username");
    assertEquals("username", foundUser.getUsername());
    assertEquals("email@email.com", foundUser.getEmail());
    assertEquals("Test user", foundUser.getFullName());
    assertEquals("image.com", foundUser.getImageUrl());
    assertEquals("Savings", foundUser.getPreferredSavingsAccount().getName());
    assertEquals("Checking", foundUser.getPreferredCheckingAccount().getName());
  }

  @Test
  void testCanEditUserDetails() {
    UserDetailsRequestDto editedUser =
        UserDetailsRequestDto.builder()
            .preferredSavingsAccountId(4L)
            .preferredCheckingAccountId(3L)
            .imageUrl("image2.com")
            .build();

    UserDetails updatedUser = userService.editUser("username", editedUser);
    assertEquals(3L, updatedUser.getPreferredCheckingAccount().getId());
    assertNotEquals(1L, updatedUser.getPreferredCheckingAccount().getId());
    assertEquals(4L, updatedUser.getPreferredSavingsAccount().getId());
    assertNotEquals(2L, updatedUser.getPreferredSavingsAccount().getId());
    assertEquals("image2.com", updatedUser.getImageUrl());
    assertNotEquals("image.com", updatedUser.getImageUrl());
    assertEquals("username", updatedUser.getUsername());
    assertEquals("Test user", updatedUser.getFullName());
  }
}
