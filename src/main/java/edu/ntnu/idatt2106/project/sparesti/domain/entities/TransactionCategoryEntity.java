package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An Entity holding information about a transaction category Object. */
@Entity
@Getter
@Setter
@Builder
@Table(name = "transaction_category")
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;

  private long suggestedAmount;
}
