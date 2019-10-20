package com.example.securedapi.controller;

import com.example.securedapi.config.JwtTokenProvider;
import com.example.securedapi.dto.AuthenticationRequest;
import com.example.securedapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final UserRepository userRepository;

  @PostMapping("/signin")
  public ResponseEntity<String> signin(@RequestBody AuthenticationRequest authenticationRequest) {
    try {
      String username = authenticationRequest.getUsername();
      String password = authenticationRequest.getPassword();
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
      authenticationManager.authenticate(authenticationToken);
      String role = userRepository.findByName(username)
              .orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRole();
      String token = tokenProvider.createToken(username, Collections.singletonList(role));
      return new ResponseEntity<>(token, HttpStatus.OK);
    } catch (RuntimeException e) {
      log.error("Error authenticating: ", e);
      return new ResponseEntity<>("Invalid username/password supplied", HttpStatus.NOT_ACCEPTABLE);
    }
  }
}
