package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entity representing a user's badge progress. */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "badge_progress")
public class BadgeProgressEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private Integer progress;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_entity_id")
  private UserEntity userEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "badge_id")
  private BadgeEntity badgeEntity;

  /** Adds one to the progress count of the badge. */
  public void increaseProgress() {
    this.progress += 1;
  }
}
