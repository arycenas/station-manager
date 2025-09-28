package com.fullstack.station_manager.service;

import com.fullstack.station_manager.dto.request.LoginRequest;
import com.fullstack.station_manager.dto.request.RegisterRequest;
import com.fullstack.station_manager.dto.response.JwtResponse;
import com.fullstack.station_manager.dto.response.SuccessResponse;
import com.fullstack.station_manager.entity.Users;
import com.fullstack.station_manager.repository.UsersRepository;
import java.util.HashMap;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UsersRepository usersRepository;

  public SuccessResponse registerUser(RegisterRequest request) {
    if (Optional.ofNullable(usersRepository.findByUsername(request.getUsername())).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already taken");
    }

    var newUser =
        Users.builder()
            .name(request.getName())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();

    usersRepository.save(newUser);

    return SuccessResponse.builder().message("User registered successfully").data(newUser).build();
  }

  public SuccessResponse loginUser(LoginRequest request) {
    var user =
        Optional.ofNullable(usersRepository.findByUsername(request.getUsername()))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
    }

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    } catch (AuthenticationException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
    }

    var token = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

    usersRepository.updateRefreshToken(request.getUsername(), refreshToken);

    JwtResponse response = JwtResponse.builder().token(token).refreshToken(refreshToken).build();

    return SuccessResponse.builder().message("User logged in successfully").data(response).build();
  }
}
