package com.fullstack.station_manager.repository;

import com.fullstack.station_manager.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsersRepository extends JpaRepository<Users, Long> {

  UserDetails findByUsername(String username);
}
