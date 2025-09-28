package com.fullstack.station_manager.service;

import com.fullstack.station_manager.dto.request.RegisterRequest;
import com.fullstack.station_manager.dto.response.RegisterResponse;
import com.fullstack.station_manager.entity.Users;
import com.fullstack.station_manager.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService implements UserDetailsService {

  private final UsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return usersRepository.findByUsername(username);
  }

  public RegisterResponse registerUser(RegisterRequest request) {
    var existingUser = usersRepository.findByUsername(request.getUsername());
    if (existingUser == null) {
      return RegisterResponse.builder().message("Username already exists").build();
    }

    var newUser =
        Users.builder()
            .name(request.getName())
            .username(request.getUsername())
            .password(request.getPassword())
            .build();

    usersRepository.save(newUser);

    return RegisterResponse.builder().message("User registered successfully").data(newUser).build();
  }
}
