package com.fullstack.station_manager.repository;

import com.fullstack.station_manager.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsersRepository extends JpaRepository<Users, Long> {

  UserDetails findByUsername(String username);

  @Modifying
  @Transactional
  @Query(
      value =
          """
            UPDATE tb_users SET tbu_token = :refreshToken WHERE tbu_username = :username
            """,
      nativeQuery = true)
  void updateRefreshToken(
      @Param("username") String username, @Param("refreshToken") String refreshToken);
}
