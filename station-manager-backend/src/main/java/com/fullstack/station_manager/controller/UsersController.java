package com.fullstack.station_manager.controller;

import com.fullstack.station_manager.dto.request.LoginRequest;
import com.fullstack.station_manager.dto.request.RegisterRequest;
import com.fullstack.station_manager.dto.response.SuccessResponse;
import com.fullstack.station_manager.service.AuthenticationService;
import com.fullstack.station_manager.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/authentication")
public class UsersController {

  private final AuthenticationService authenticationService;

  @PostMapping("register")
  public ResponseEntity<SuccessResponse> registerUser(@RequestBody RegisterRequest request) {
    var response = authenticationService.registerUser(request);

    return ResponseEntity.ok(response);
  }

  @PostMapping("login")
  public ResponseEntity<SuccessResponse> login(@RequestBody LoginRequest request) {
    var response = authenticationService.loginUser(request);

    return ResponseEntity.ok(response);
  }
}
