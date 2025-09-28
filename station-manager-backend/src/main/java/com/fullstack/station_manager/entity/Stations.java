package com.fullstack.station_manager.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "stations")
public class Stations {

  @Field(name = "station_uri")
  private String stationUri;

  @Field(name = "station_agency")
  private String stationAgency;

  @Field(name = "station_name")
  private String stationName;

  @Field(name = "station_routes")
  private List<Object> stationRoutes;
}
