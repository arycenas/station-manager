package com.fullstack.station_manager.utility;

import com.fullstack.station_manager.service.JwtService;
import com.fullstack.station_manager.service.UsersService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UsersService usersService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Skip JWT processing for authentication endpoints
    String requestPath = request.getRequestURI();

    if (requestPath.startsWith("/api/authentication/")) {
      filterChain.doFilter(request, response);

      return;
    }

    final String authorizationHeader = request.getHeader("Authorization");

    if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);

      return;
    }

    final String jwtToken = authorizationHeader.substring(7);

    try {
      final String username = jwtService.extractUsernameFromToken(jwtToken);

      if (StringUtils.isNotEmpty(username)
          && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = usersService.loadUserByUsername(username);

        validateToken(userDetails, jwtToken, request);
      }
    } catch (Exception e) {
      log.error("JwtUtil: Error processing JWT token: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private void validateToken(UserDetails userDetails, String jwtToken, HttpServletRequest request) {
    boolean isValid = jwtService.validateToken(jwtToken, userDetails);
    log.info(
        "JwtUtil: Token validation result: {} for {} {}",
        isValid,
        request.getMethod(),
        request.getRequestURI());

    if (isValid) {
      SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

      UsernamePasswordAuthenticationToken token =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      securityContext.setAuthentication(token);
      SecurityContextHolder.setContext(securityContext);
    } else {
      log.info(
          "JwtUtil: Token validation failed for user: {} for {} {}",
          userDetails.getUsername(),
          request.getMethod(),
          request.getRequestURI());
    }
  }
}
