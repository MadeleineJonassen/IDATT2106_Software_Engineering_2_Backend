package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingChallengeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingGoalEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository providing CRUD functionality against saving challenges. */
@Repository
public interface SavingChallengeRepository extends JpaRepository<SavingChallengeEntity, Long> {}
