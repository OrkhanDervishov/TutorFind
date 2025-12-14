package com.team13.TutorFind.Database.Repos;

import com.team13.TutorFind.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepo extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
}