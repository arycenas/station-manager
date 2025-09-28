package com.fullstack.station_manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fullstack.station_manager.dto.response.SuccessResponse;
import com.fullstack.station_manager.entity.Stations;
import com.fullstack.station_manager.repository.StationsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationsService {

  private final RestTemplate restTemplate;
  private final StationsRepository stationsRepository;

  @Value("${station.uri}")
  private String stationUri;

  public SuccessResponse stationList() {
    var stations = stationsRepository.findAll();

    if (stations.isEmpty()) {
      return SuccessResponse.builder().message("No stations found").build();
    }

    return SuccessResponse.builder()
        .message("Successfully fetched all stations")
        .data(stations)
        .build();
  }

  public SuccessResponse saveStations() {
    try {
      log.info("Fetching stations from external API: {}", stationUri);
      
      var externalResponse =
          restTemplate
              .exchange(
                  stationUri,
                  HttpMethod.GET,
                  null,
                  com.fullstack.station_manager.dto.external.ExternalStationResponse.class)
              .getBody();

      log.info("Response from external API: {}", externalResponse);

      if (externalResponse != null && externalResponse.getStops() != null) {
        // Map external response to internal Stations entities
        var stations = mapExternalResponseToStations(externalResponse);
        
        if (!stations.isEmpty()) {
          // Clear existing stations before saving new ones
          stationsRepository.deleteAll();
          stationsRepository.saveAll(stations);
          log.info("Successfully saved {} stations to database", stations.size());
          
          return SuccessResponse.builder()
              .message("Stations saved successfully")
              .data(stations)
              .build();
        } else {
          log.warn("No valid stations data to save");
          return SuccessResponse.builder()
              .message("No valid stations data to save")
              .build();
        }
      } else {
        log.warn("No stations data received from external API");
        return SuccessResponse.builder()
            .message("No stations data available to save")
            .build();
      }
      
    } catch (Exception e) {
      log.error("Error fetching stations from external API: {}", e.getMessage(), e);
      return SuccessResponse.builder()
          .message("Failed to fetch stations from external API: " + e.getMessage())
          .build();
    }
  }

  private List<Stations> mapExternalResponseToStations(com.fullstack.station_manager.dto.external.ExternalStationResponse externalResponse) {
    return externalResponse.getStops().stream()
        .map(this::mapStopToStation)
        .toList();
  }

  private Stations mapStopToStation(com.fullstack.station_manager.dto.external.ExternalStationResponse.Stop stop) {
    return Stations.builder()
        .stationUri(stop.getUri())
        .stationAgency(stop.getAgency())
        .stationName(stop.getName())
        .stationRoutes(mapRoutesToStationRoutes(stop.getRoutes()))
        .build();
  }

  private List<Object> mapRoutesToStationRoutes(List<com.fullstack.station_manager.dto.external.ExternalStationResponse.Route> routes) {
    if (routes == null) {
      return java.util.List.of();
    }
    
    return routes.stream()
        .map(this::mapRouteToStationRoute)
        .collect(java.util.stream.Collectors.toList());
  }

  private java.util.Map<String, Object> mapRouteToStationRoute(com.fullstack.station_manager.dto.external.ExternalStationResponse.Route route) {
    String routeGroupId = route.getRouteGroupId() != null ? route.getRouteGroupId() : "";
    String uri = route.getUri() != null ? route.getUri() : "";
    String name = route.getName() != null ? route.getName() : "";
    
    var stopTimes = mapStopTimes(route.getStopTimes());
    
    return java.util.Map.of(
        "routeGroupId", routeGroupId,
        "uri", uri,
        "name", name,
        "stopTimesCount", stopTimes.size(),
        "stopTimes", stopTimes
    );
  }

  private java.util.List<java.util.Map<String, Object>> mapStopTimes(java.util.List<com.fullstack.station_manager.dto.external.ExternalStationResponse.StopTime> stopTimes) {
    if (stopTimes == null) {
      return java.util.List.of();
    }
    
    return stopTimes.stream()
        .map(this::mapStopTime)
        .toList();
  }

  private java.util.Map<String, Object> mapStopTime(com.fullstack.station_manager.dto.external.ExternalStationResponse.StopTime stopTime) {
    Integer serviceIdValue = stopTime.getServiceId();
    int serviceId = serviceIdValue != null ? serviceIdValue : 0;
    
    String departureTime = stopTime.getDepartureTime() != null ? stopTime.getDepartureTime() : "";
    
    Long departureTimestampValue = stopTime.getDepartureTimestamp();
    long departureTimestamp = departureTimestampValue != null ? departureTimestampValue : 0L;
    
    String shape = stopTime.getShape() != null ? stopTime.getShape() : "";
    
    return java.util.Map.of(
        "serviceId", serviceId,
        "departureTime", departureTime,
        "departureTimestamp", departureTimestamp,
        "shape", shape
    );
  }
}
