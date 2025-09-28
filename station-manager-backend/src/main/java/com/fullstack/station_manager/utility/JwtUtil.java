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
    String requestMethod = request.getMethod();
    log.info("JwtUtil: Processing request - Method: {}, Path: {}", requestMethod, requestPath);

    if (requestPath.startsWith("/api/authentication/")) {
      log.info("JwtUtil: Skipping JWT processing for authentication endpoint");
      filterChain.doFilter(request, response);
      return;
    }

    final String authorizationHeader = request.getHeader("Authorization");
    log.info(
        "JwtUtil: Authorization header: {}",
        authorizationHeader != null
            ? authorizationHeader.substring(0, Math.min(50, authorizationHeader.length())) + "..."
            : "null");

    if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
      log.warn("JwtUtil: No valid authorization header found, proceeding without authentication");
      filterChain.doFilter(request, response);

      return;
    }

    final String jwtToken = authorizationHeader.substring(7);
    log.info(
        "JwtUtil: Processing token: {}...", jwtToken.substring(0, Math.min(20, jwtToken.length())));

    try {
      final String username = jwtService.extractUsernameFromToken(jwtToken);
      log.info("JwtUtil: Extracted username: {}", username);

      if (StringUtils.isNotEmpty(username)
          && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = usersService.loadUserByUsername(username);
        log.info("JwtUtil: Found user details for: {}", userDetails.getUsername());

        validateToken(userDetails, jwtToken, request);
      }
    } catch (Exception e) {
      log.error("JwtUtil: Error processing JWT token: {}", e.getMessage());
      e.printStackTrace();
    }

    log.info(
        "JwtUtil: Final authentication context before proceeding: {}",
        SecurityContextHolder.getContext().getAuthentication());
    filterChain.doFilter(request, response);
  }

  private void validateToken(UserDetails userDetails, String jwtToken, HttpServletRequest request) {
    log.info(
        "JwtUtil: Validating token for user: {} for {} {}",
        userDetails.getUsername(),
        request.getMethod(),
        request.getRequestURI());
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
      log.info(
          "JwtUtil: Authentication set successfully for user: {} for {} {}",
          userDetails.getUsername(),
          request.getMethod(),
          request.getRequestURI());
      log.info(
          "JwtUtil: SecurityContext authentication: {}",
          SecurityContextHolder.getContext().getAuthentication());
    } else {
      log.info(
          "JwtUtil: Token validation failed for user: {} for {} {}",
          userDetails.getUsername(),
          request.getMethod(),
          request.getRequestURI());
    }
  }
}
