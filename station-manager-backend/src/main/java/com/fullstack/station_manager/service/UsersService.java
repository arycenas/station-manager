package com.fullstack.station_manager.service;

import com.fullstack.station_manager.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService implements UserDetailsService {

  private final UsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails userDetails = usersRepository.findByUsername(username);

    if (userDetails == null) {
      log.error("UsersService: User not found");

      throw new UsernameNotFoundException("User not found: " + username);
    }

    return userDetails;
  }
}
