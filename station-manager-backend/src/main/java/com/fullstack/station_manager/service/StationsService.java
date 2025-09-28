package com.fullstack.station_manager.service;

import com.fullstack.station_manager.dto.response.SuccessResponse;
import com.fullstack.station_manager.entity.Stations;
import com.fullstack.station_manager.repository.StationsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

  public SuccessResponse saveStations() {
    var response =
        restTemplate
            .exchange(
                stationUri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Stations>>() {})
            .getBody();

    log.info("Response from StationsService: {}", response);

    stationsRepository.saveAll(response);

    return SuccessResponse.builder().message("Stations saved successfully").data(response).build();
  }
}
