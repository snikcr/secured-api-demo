package com.example.securedapi.config;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JwtTokenProvider {

  @Value("${security.jwt.token.secret-key:secret}")
  private String secretKey;

  @Value("${security.jwt.token.expire-length:3600000}")
  private long validityInMilliseconds; // 1h

  private final UserDetailsService userDetailsService;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(String username, List<String> roles) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put("roles", roles);

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    String jwt = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(validity)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    log.info("Generated JWT: {}", jwt);
    return jwt;
  }

  public Authentication getAuthentication(String token) {
    UserDetails details = userDetailsService.loadUserByUsername(getUserName(token));
    return new UsernamePasswordAuthenticationToken(details,  "", details.getAuthorities());
  }

  public String getUserName(String token) {
    return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

  public String resolveToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    return Optional.ofNullable(token)
            .filter(s -> s.startsWith("Bearer "))
            .map(s -> s.substring(7))
            .orElse(null);
  }

  public boolean validateToken(String token) {
    log.info("Validating token: {}", token);
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Error validating token: ", e);
      throw new RuntimeException("Expired or ivalid token", e);
    }
  }
}
