package com.fullstack.station_manager.service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fullstack.station_manager.utility.Constant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  public String generateToken(UserDetails userDetails) {
    var now = new Date(System.currentTimeMillis());
    var expired = new Date(now.getTime() + 1000 * 60 * 60 * 24);

    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(now)
        .expiration(expired)
        .signWith(getSigningKey())
        .compact();
  }

  public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    var now = new Date(System.currentTimeMillis());
    var expired = new Date(now.getTime() + 432000000);

    return Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(now)
        .expiration(expired)
        .signWith(getSigningKey())
        .compact();
  }

  public String extractUsernameFromToken(String token) {
    return getClaimsFromToken(token, Claims::getSubject);
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsernameFromToken(token);

    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public boolean validateTokenFromClient(String token) {
      var tokenIsExpired = isTokenExpired(token);

      return !tokenIsExpired;
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(Constant.KeyBytes.KEY);

    return Keys.hmacShaKeyFor(keyBytes);
  }

  private <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaimsFromToken(token);

    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaimsFromToken(String token) {
    return Jwts.parser()
        .verifyWith((SecretKey) getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private boolean isTokenExpired(String token) {
    return getClaimsFromToken(token, Claims::getExpiration).before(new Date());
  }
}
