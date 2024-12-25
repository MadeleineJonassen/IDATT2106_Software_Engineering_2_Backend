package edu.ntnu.idatt2106.project.sparesti.repositories;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository responsible for providing database operations against the badge repository. */
@Repository
public interface BadgeRepository extends JpaRepository<BadgeEntity, Long> {
  BadgeEntity findBadgeEntityByName(String name);
}
