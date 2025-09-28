package com.fullstack.station_manager.service;

import com.fullstack.station_manager.dto.external.ExternalStationResponse;
import com.fullstack.station_manager.dto.response.SuccessResponse;
import com.fullstack.station_manager.entity.Stations;
import com.fullstack.station_manager.repository.StationsRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

      var response =
          restTemplate
              .exchange(stationUri, HttpMethod.GET, null, ExternalStationResponse.class)
              .getBody();

      log.info("Response from external API: {}", response);

      if (response != null && response.getStops() != null) {
        // Map external response to internal Stations entities
        var stations = mapExternalResponseToStations(response);

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

          return SuccessResponse.builder().message("No valid stations data to save").build();
        }
      } else {
        log.warn("No stations data received from external API");

        return SuccessResponse.builder().message("No stations data available to save").build();
      }

    } catch (Exception e) {
      log.error("Error fetching stations from external API: {}", e.getMessage(), e);

      return SuccessResponse.builder()
          .message("Failed to fetch stations from external API: " + e.getMessage())
          .build();
    }
  }

  private List<Stations> mapExternalResponseToStations(ExternalStationResponse response) {
    return response.getStops().stream().map(this::mapStopToStation).toList();
  }

  private Stations mapStopToStation(ExternalStationResponse.Stop stop) {
    return Stations.builder()
        .stationUri(stop.getUri())
        .stationAgency(stop.getAgency())
        .stationName(stop.getName())
        .stationRoutes(mapRoutesToStationRoutes(stop.getRoutes()))
        .build();
  }

  private List<Object> mapRoutesToStationRoutes(List<ExternalStationResponse.Route> routes) {
    if (routes == null) return List.of();

    return routes.stream().map(this::mapRouteToStationRoute).collect(Collectors.toList());
  }

  private Map<String, Object> mapRouteToStationRoute(ExternalStationResponse.Route route) {
    var routeGroupId = route.getRouteGroupId() != null ? route.getRouteGroupId() : "";
    var uri = route.getUri() != null ? route.getUri() : "";
    var name = route.getName() != null ? route.getName() : "";

    var stopTimes = mapStopTimes(route.getStopTimes());

    return java.util.Map.of(
        "routeGroupId", routeGroupId,
        "uri", uri,
        "name", name,
        "stopTimesCount", stopTimes.size(),
        "stopTimes", stopTimes);
  }

  private List<Map<String, Object>> mapStopTimes(List<ExternalStationResponse.StopTime> stopTimes) {
    if (stopTimes == null) {
      return List.of();
    }

    return stopTimes.stream().map(this::mapStopTime).toList();
  }

  private Map<String, Object> mapStopTime(ExternalStationResponse.StopTime stopTime) {
    var serviceIdValue = stopTime.getServiceId();
    var serviceId = serviceIdValue != null ? serviceIdValue : 0;

    var departureTime = stopTime.getDepartureTime() != null ? stopTime.getDepartureTime() : "";

    var departureTimestampValue = stopTime.getDepartureTimestamp();
    var departureTimestamp = departureTimestampValue != null ? departureTimestampValue : 0L;

    String shape = stopTime.getShape() != null ? stopTime.getShape() : "";

    return Map.of(
        "serviceId", serviceId,
        "departureTime", departureTime,
        "departureTimestamp", departureTimestamp,
        "shape", shape);
  }
}
