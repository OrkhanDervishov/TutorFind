package com.team13.TutorFind.Database.Repos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team13.TutorFind.User.TutorDetails;

@Repository
public interface TutorDetailsRepo extends JpaRepository<TutorDetails, Long> {
    
    // Find tutor profile by user ID (NEW - ADDED FOR AVAILABILITY API)
    Optional<TutorDetails> findByUserId(Long userId);
    
    // Find tutors by city
    List<TutorDetails> findByCityId(Long cityId);
    
    // Find active tutors
    List<TutorDetails> findByIsActiveTrue();
    
    // Search with filters
    @Query("SELECT t FROM TutorDetails t WHERE " +
           "(:cityId IS NULL OR t.cityId = :cityId) AND " +
           "(:minPrice IS NULL OR t.monthlyRate >= :minPrice) AND " +
           "(:maxPrice IS NULL OR t.monthlyRate <= :maxPrice) AND " +
           "(:minRating IS NULL OR t.ratingAvg >= :minRating) AND " +
           "t.isActive = true " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'rating' THEN t.ratingAvg END DESC, " +
           "CASE WHEN :sortBy = 'price_asc' THEN t.monthlyRate END ASC, " +
           "CASE WHEN :sortBy = 'price_desc' THEN t.monthlyRate END DESC")
    List<TutorDetails> searchTutors(
        @Param("cityId") Long cityId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minRating") Float minRating,
        @Param("sortBy") String sortBy
    );
    List<TutorDetails> findByIsVerified(Boolean isVerified);
    Long countByIsVerified(Boolean isVerified);
}