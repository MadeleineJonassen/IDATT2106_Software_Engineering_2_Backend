package edu.ntnu.idatt2106.project.sparesti.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/** A configuration class used for decoding jwts. */
@Configuration
public class JwtDecoderConfig {

  @Bean
  public JwtDecoder jwtDecoder() {
    String jwkSetUri = "https://dev-2nle7jf7nt5rhoan.eu.auth0.com/.well-known/jwks.json";
    return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
  }
}
