package edu.ntnu.idatt2106.project.sparesti.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("test") // This configuration is only active when the "test" profile is active
public class SecurityTestConfig {

  @Bean
  public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable() // Disable CSRF for testing
        .authorizeHttpRequests(
            authorize ->
                authorize.anyRequest().permitAll() // Permit all requests without authentication
            );
    return http.build();
  }
}
