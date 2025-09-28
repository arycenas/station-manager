package com.fullstack.station_manager.controller;

import com.fullstack.station_manager.dto.response.SuccessResponse;
import com.fullstack.station_manager.service.StationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/stations")
@RequiredArgsConstructor
public class StationsController {

  private final StationsService stationsService;

  @GetMapping
  public ResponseEntity<SuccessResponse> stations() {
    var response = stationsService.stationList();

    return ResponseEntity.ok(response);
  }

  @PostMapping("save")
  public ResponseEntity<SuccessResponse> saveStations() {
    log.info("StationsController: POST /stations/save endpoint called");
    log.info(
        "StationsController: Authentication context: {}",
        SecurityContextHolder.getContext().getAuthentication());

    var response = stationsService.saveStations();

    return ResponseEntity.ok(response);
  }
}
