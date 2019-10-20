package com.example.securedapi.service;

import com.example.securedapi.model.User;
import com.example.securedapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    UserDetails details = userRepository.findByName(s)
            .map(this::buildUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException("User with name " + s + " not found."));
    log.info("Recovered UserDetails {}", details);
    return details;
  }

  private UserDetails buildUserDetails(User user) {
    List<GrantedAuthority> authorities = Stream.of(user.getRole())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    return new org.springframework.security.core.userdetails.User(
            user.getName(), user.getPassword(), authorities);
  }
}
