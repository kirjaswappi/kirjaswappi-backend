/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.common.jpa.daos.AdminUserDao;

@Component
public class JwtUtil {
  private static final String SECRET_STRING = "admin-secretgsfgsgsergergergerwgewrgewrgwergwergwregre";
  private static final byte[] SECRET_KEY_BYTES = SECRET_STRING.getBytes(StandardCharsets.UTF_8);
  private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_BYTES);
  private static final long TOKEN_EXPIRATION_MS = 30 * 60 * 1000;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(SECRET_KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token)
        .before(new Date());
  }

  public String generateToken(AdminUserDao userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("Scope", "Admin");
    return createToken(claims, userDetails.getUsername());
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
        .signWith(SECRET_KEY)
        .compact();
  }

  public boolean validateToken(String token, AdminUserDao userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
