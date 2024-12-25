package edu.ntnu.idatt2106.project.sparesti.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/** A configuration class used for configuring the security filter chain. */
@Configuration
public class SecurityConfig {

  /**
   * Configures the security filter chain.
   *
   * @param http The HttpSecurity object to configure.
   * @return The SecurityFilterChain object.
   * @throws Exception If an error occurs.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/public/**"))
        .authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers(HttpMethod.GET, "/api/public/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/public/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.PATCH, "/api/public/**")
                    .permitAll()
                    .requestMatchers(
                        "/v3/api-docs",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**")
                    .permitAll()
                    .requestMatchers("/api/secure/**")
                    .authenticated())
        .cors(withDefaults())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
        .build();
  }
}
