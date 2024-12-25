package edu.ntnu.idatt2106.project.sparesti.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A dto holding information about a message. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

  private String message;
}
