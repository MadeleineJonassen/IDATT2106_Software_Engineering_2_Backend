package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingChallengeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingGoalEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository providing CRUD functionality against saving goals. */
public interface SavingGoalRepository extends JpaRepository<SavingGoalEntity, Long> {}
