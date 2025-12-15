package com.team13.TutorFind.Database.Repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team13.TutorFind.entity.AvailabilitySlot;

@Repository
public interface AvailabilitySlotRepo extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByTutorIdAndIsActiveTrue(Long tutorId);
    List<AvailabilitySlot> findByTutorIdOrderByDayOfWeekAscStartTimeAsc(Long tutorId);
    List<AvailabilitySlot> findByTutorId(Long tutorId);
}