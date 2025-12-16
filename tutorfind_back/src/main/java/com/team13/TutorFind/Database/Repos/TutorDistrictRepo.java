package com.team13.TutorFind.Database.Repos;

import com.team13.TutorFind.entity.TutorDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorDistrictRepo extends JpaRepository<TutorDistrict, Long> {
    List<TutorDistrict> findByTutorId(Long tutorId);
}