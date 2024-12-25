package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An Entity holding information about a badge Object. */
@Entity
@Getter
@Setter
@Builder
@Table(name = "badge")
@AllArgsConstructor
@NoArgsConstructor
public class BadgeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;

  private String description;

  @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL)
  private List<BadgeTierEntity> badgeTiers;

  /*
  @ManyToMany(mappedBy = "badges", fetch = FetchType.LAZY)
  private List<UserEntity> users;*/
}
