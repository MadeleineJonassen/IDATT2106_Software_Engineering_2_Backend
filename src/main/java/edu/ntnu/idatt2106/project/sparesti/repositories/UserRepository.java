package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.SavingChallengeEntity;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Provides basic CRUD functionality against the database through JPA. */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Boolean existsUserEntityByEmailContainingIgnoreCase(String email);

  Optional<UserEntity> findUserEntityByUsername(String username);

  List<UserEntity> findAllByOrderByScoreDesc();
}
