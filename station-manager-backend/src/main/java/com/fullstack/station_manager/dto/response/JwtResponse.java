package com.fullstack.station_manager.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {

  private String token;
  private String refreshToken;
}
