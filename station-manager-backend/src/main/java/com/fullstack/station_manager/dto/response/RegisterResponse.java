package com.fullstack.station_manager.dto.response;

import com.fullstack.station_manager.entity.Users;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {

  private String message;
  private Users data;
}
