package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/** Entity representing a counter value stored in the database. Used for testing purposes. */
@Entity
@Getter
@Setter
@Table(name = "test_id_counter")
public class TestIdCounterEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
}
