package com.fullstack.station_manager.controller;

import com.fullstack.station_manager.dto.request.LoginRequest;
import com.fullstack.station_manager.dto.request.RegisterRequest;
import com.fullstack.station_manager.dto.response.SuccessResponse;
import com.fullstack.station_manager.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/authentication")
public class UsersController {

  private final UsersService usersService;

  @PostMapping("register")
  public ResponseEntity<SuccessResponse> registerUser(RegisterRequest request) {
    var response = usersService.registerUser(request);

    return ResponseEntity.ok(response);
  }

  @PostMapping("login")
  public ResponseEntity<SuccessResponse> login(LoginRequest request) {
    var response = usersService.loginUser(request);

    return ResponseEntity.ok(response);
  }
}
