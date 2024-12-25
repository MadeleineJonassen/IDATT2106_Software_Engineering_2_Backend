package edu.ntnu.idatt2106.project.sparesti.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Responsible for mapping objects. */
@Configuration
public class MapperConfig {

  /**
   * Configures the model mapper.
   *
   * @return The configured ModelMapper object.
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    return modelMapper;
  }
}
