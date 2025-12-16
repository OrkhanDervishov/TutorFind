package com.team13.TutorFind.Database.Repos;

import com.team13.TutorFind.entity.TutorSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorSubjectRepo extends JpaRepository<TutorSubject, Long> {
    List<TutorSubject> findByTutorId(Long tutorId);
}