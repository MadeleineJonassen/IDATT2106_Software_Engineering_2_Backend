package edu.ntnu.idatt2106.project.sparesti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for setting up WebClient in a Spring WebFlux application. This class provides
 * a centralized configuration for WebClient, which can be autowired and used in other parts of the
 * application to make HTTP requests.
 */
@Configuration
public class WebClientConfig {

  /**
   * Creates a singleton bean of WebClient for use throughout the application. The WebClient
   * instance is configured with default settings suitable for general use.
   *
   * @return A configured WebClient instance.
   */
  @Bean
  public WebClient webClient() {
    return WebClient.builder().build();
  }
}
