package com.team13.TutorFind.Database.Repos;

import com.team13.TutorFind.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepo extends JpaRepository<District, Long> {
    List<District> findByCityId(Long cityId);
    List<District> findByNameContainingIgnoreCase(String name);
}