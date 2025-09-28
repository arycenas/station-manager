package com.fullstack.station_manager.dto.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExternalStationResponse {

  private String name;
  private String uri;
  private List<Stop> stops;

  @Data
  public static class Stop {

    private String agency;
    private String uri;
    private String name;
    private List<Route> routes;
  }

  @Data
  public static class Route {

    @JsonProperty("route_group_id")
    private String routeGroupId;

    private String uri;
    private String name;

    @JsonProperty("stop_times")
    private List<StopTime> stopTimes;
  }

  @Data
  public static class StopTime {

    @JsonProperty("service_id")
    private Integer serviceId;

    @JsonProperty("departure_time")
    private String departureTime;

    @JsonProperty("departure_timestamp")
    private Long departureTimestamp;

    private String shape;
  }
}
