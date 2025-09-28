package com.fullstack.station_manager.repository;

import com.fullstack.station_manager.entity.Stations;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StationsRepository extends MongoRepository<Stations, String> {}
