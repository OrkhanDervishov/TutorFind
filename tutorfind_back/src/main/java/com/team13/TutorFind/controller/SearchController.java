package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Services.SearchService;
import com.team13.TutorFind.dto.SearchRequest;
import com.team13.TutorFind.dto.TutorSearchResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.team13.TutorFind.dto.TutorProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/tutors")
public class SearchController {
    
    private final SearchService searchService;
    
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    @GetMapping
    public ResponseEntity<List<TutorSearchResult>> searchTutors(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false, defaultValue = "rating") String sortBy
    ) {
        // Build search request
        SearchRequest request = new SearchRequest();
        request.setCity(city);
        request.setDistrict(district);
        request.setSubject(subject);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setMinRating(minRating);
        request.setSortBy(sortBy);
        
        // Search
        List<TutorSearchResult> results = searchService.searchTutors(request);
        
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTutorProfile(@PathVariable Long id) {
        try {
            TutorProfileResponse profile = searchService.getTutorProfile(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Tutor not found: " + e.getMessage());
        }
    }
}