package edu.ntnu.idatt2106.project.sparesti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Main class for running Spring Boot application. */
@SpringBootApplication
@EnableScheduling
public class SparestiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SparestiApplication.class, args);
  }
}
