package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entity holding information about a tier of a badge. */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "badge_tier")
@Getter
@Setter
@Builder
public class BadgeTierEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String description;

  private Integer tier;

  private Integer target;

  @ManyToOne
  @JoinColumn(name = "badge_id")
  private BadgeEntity badge;
}
