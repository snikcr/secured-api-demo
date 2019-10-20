package com.example.securedapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

  private final JwtTokenProvider provider;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    String token = provider.resolveToken((HttpServletRequest) servletRequest);
    Authentication authentication = token != null && provider.validateToken(token) ? provider.getAuthentication(token) : null;
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
