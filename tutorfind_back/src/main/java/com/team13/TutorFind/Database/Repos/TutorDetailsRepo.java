package com.team13.TutorFind.Database.Repos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.team13.TutorFind.User.TutorDetails;

@Repository
public interface TutorDetailsRepo extends JpaRepository<TutorDetails, Long> {
    
    // Find tutor profile by user ID (NEW - ADDED FOR AVAILABILITY API)
    Optional<TutorDetails> findByUserId(Long userId);
    
    // Find tutors by city
    List<TutorDetails> findByCityId(Long cityId);
    
    // Find active tutors
    List<TutorDetails> findByIsActiveTrue();
    
    // Search with filters (district, subject, availability) + pagination
    @Query("""
        SELECT DISTINCT t FROM TutorDetails t
        LEFT JOIN TutorSubject ts ON ts.tutorId = t.id
        LEFT JOIN Subject s ON s.id = ts.subjectId
        LEFT JOIN TutorDistrict td ON td.tutorId = t.id
        LEFT JOIN District d ON d.id = td.districtId
        LEFT JOIN AvailabilitySlot a ON a.tutorId = t.id
        WHERE t.isActive = true
          AND (:cityId IS NULL OR t.cityId = :cityId)
          AND (:district IS NULL OR d.name = :district)
          AND (:subject IS NULL OR s.name = :subject)
          AND (:minPrice IS NULL OR t.monthlyRate >= :minPrice)
          AND (:maxPrice IS NULL OR t.monthlyRate <= :maxPrice)
          AND (:minRating IS NULL OR t.ratingAvg >= :minRating)
          AND (a.id IS NULL OR a.dayOfWeek = COALESCE(:availabilityDay, a.dayOfWeek))
          AND (a.id IS NULL OR a.startTime <= COALESCE(:availabilityStart, a.startTime))
          AND (a.id IS NULL OR a.endTime >= COALESCE(:availabilityEnd, a.endTime))
        """)
    Page<TutorDetails> searchTutors(
        @Param("cityId") Long cityId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minRating") Float minRating,
        @Param("district") String district,
        @Param("subject") String subject,
        @Param("availabilityDay") String availabilityDay,
        @Param("availabilityStart") java.time.LocalTime availabilityStart,
        @Param("availabilityEnd") java.time.LocalTime availabilityEnd,
        Pageable pageable
    );
    List<TutorDetails> findByIsVerified(Boolean isVerified);
    Long countByIsVerified(Boolean isVerified);
}
