package com.fullstack.station_manager.controller;

import com.fullstack.station_manager.dto.response.SuccessResponse;
import com.fullstack.station_manager.service.StationsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/stations")
public class StationsController {

  private final StationsService stationsService;

  public StationsController(StationsService stationsService) {
    this.stationsService = stationsService;
  }

  @PostMapping("save")
  public ResponseEntity<SuccessResponse> saveStations() {
    var response = stationsService.saveStations();

    return ResponseEntity.ok(response);
  }
}
