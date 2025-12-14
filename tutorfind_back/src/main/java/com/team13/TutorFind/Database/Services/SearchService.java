package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.CityRepo;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.Database.Repos.TutorSubjectRepo;
import com.team13.TutorFind.Database.Repos.TutorDistrictRepo;
import com.team13.TutorFind.Database.Repos.AvailabilitySlotRepo;
import com.team13.TutorFind.Database.Repos.DistrictRepo;
import com.team13.TutorFind.Database.Repos.SubjectRepo;  // ← ADD THIS
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.dto.SearchRequest;
import com.team13.TutorFind.dto.TutorSearchResult;
import com.team13.TutorFind.dto.TutorProfileResponse;
import com.team13.TutorFind.entity.City;
import com.team13.TutorFind.entity.District;  // ← ADD THIS
import com.team13.TutorFind.entity.Subject;   // ← ADD THIS TOO
import com.team13.TutorFind.entity.TutorSubject;
import com.team13.TutorFind.entity.TutorDistrict;
import com.team13.TutorFind.entity.AvailabilitySlot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    
    private final TutorDetailsRepo tutorDetailsRepo;
    private final UserRepo userRepo;
    private final CityRepo cityRepo;
    private final TutorSubjectRepo tutorSubjectRepo;
    private final TutorDistrictRepo tutorDistrictRepo;
    private final AvailabilitySlotRepo availabilitySlotRepo;
    private final SubjectRepo subjectRepo;
    private final DistrictRepo districtRepo;

    public SearchService(TutorDetailsRepo tutorDetailsRepo, 
                        UserRepo userRepo,
                        CityRepo cityRepo,
                        TutorSubjectRepo tutorSubjectRepo,
                        TutorDistrictRepo tutorDistrictRepo,
                        AvailabilitySlotRepo availabilitySlotRepo,
                        SubjectRepo subjectRepo,
                        DistrictRepo districtRepo) {
        this.tutorDetailsRepo = tutorDetailsRepo;
        this.userRepo = userRepo;
        this.cityRepo = cityRepo;
        this.tutorSubjectRepo = tutorSubjectRepo;
        this.tutorDistrictRepo = tutorDistrictRepo;
        this.availabilitySlotRepo = availabilitySlotRepo;
        this.subjectRepo = subjectRepo;
        this.districtRepo = districtRepo;
    }

    public List<TutorSearchResult> searchTutors(SearchRequest request) {
        // Get cityId from city name
        Long cityId = null;
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            City city = cityRepo.findByName(request.getCity()).orElse(null);
            if (city != null) {
                cityId = city.getId();
            }
        }
        
        // Search tutors
        List<TutorDetails> tutors = tutorDetailsRepo.searchTutors(
            cityId,
            request.getMinPrice(),
            request.getMaxPrice(),
            request.getMinRating(),
            request.getSortBy() != null ? request.getSortBy() : "rating"
        );
        
        // Convert to DTOs
        List<TutorSearchResult> results = new ArrayList<>();
        for (TutorDetails tutor : tutors) {
            TutorSearchResult result = convertToDTO(tutor);
            if (result != null) {
                results.add(result);
            }
        }
        
        return results;
    }
    
    private TutorSearchResult convertToDTO(TutorDetails tutor) {
        TutorSearchResult result = new TutorSearchResult();
        
        result.setId(tutor.getId());
        result.setUserId(tutor.getUserId());
        result.setHeadline(tutor.getHeadline());
        result.setBio(tutor.getBio());
        result.setMonthlyRate(tutor.getMonthlyRate());
        result.setRating(tutor.getRatingAvg());
        result.setReviewCount(tutor.getRatingCount());
        result.setExperienceYears(tutor.getExperienceYears());
        result.setIsVerified(tutor.getIsVerified());
        
        // Get user info
        if (tutor.getUserId() != null) {
            User user = userRepo.findById(tutor.getUserId()).orElse(null);
            if (user != null) {
                result.setFirstName(user.getFirstName());
                result.setLastName(user.getLastName());
            }
        }
        
        // Get city name
        if (tutor.getCityId() != null) {
            City city = cityRepo.findById(tutor.getCityId()).orElse(null);
            if (city != null) {
                result.setCity(city.getName());
            }
        }
        
        return result;
    }
    
    public TutorProfileResponse getTutorProfile(Long tutorId) {
        // Get tutor profile
        TutorDetails tutor = tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        TutorProfileResponse response = new TutorProfileResponse();
        
        // Basic info
        response.setId(tutor.getId());
        response.setUserId(tutor.getUserId());
        response.setHeadline(tutor.getHeadline());
        response.setBio(tutor.getBio());
        response.setQualifications(tutor.getQualifications());
        response.setExperienceYears(tutor.getExperienceYears());
        response.setMonthlyRate(tutor.getMonthlyRate());
        response.setRating(tutor.getRatingAvg());
        response.setReviewCount(tutor.getRatingCount());
        response.setIsVerified(tutor.getIsVerified());
        response.setIsActive(tutor.getIsActive());
        
        // Get user info
        if (tutor.getUserId() != null) {
            User user = userRepo.findById(tutor.getUserId()).orElse(null);
            if (user != null) {
                response.setFirstName(user.getFirstName());
                response.setLastName(user.getLastName());
                response.setEmail(user.getEmail());
                response.setPhoneNumber(user.getPhoneNumber());
            }
        }
        
        // Get city
        if (tutor.getCityId() != null) {
            City city = cityRepo.findById(tutor.getCityId()).orElse(null);
            if (city != null) {
                response.setCity(city.getName());
            }
        }
        
        // Get districts
        List<String> districtNames = new ArrayList<>();
        List<TutorDistrict> tutorDistricts = tutorDistrictRepo.findByTutorId(tutorId);
        for (TutorDistrict td : tutorDistricts) {
            District district = districtRepo.findById(td.getDistrictId()).orElse(null);
            if (district != null) {
                districtNames.add(district.getName());
            }
        }
        response.setDistricts(districtNames);
        
        // Get subjects
        List<TutorProfileResponse.SubjectInfo> subjectInfos = new ArrayList<>();
        List<TutorSubject> tutorSubjects = tutorSubjectRepo.findByTutorId(tutorId);
        for (TutorSubject ts : tutorSubjects) {
            Subject subject = subjectRepo.findById(ts.getSubjectId()).orElse(null);
            if (subject != null) {
                TutorProfileResponse.SubjectInfo info = new TutorProfileResponse.SubjectInfo(
                    subject.getId(),
                    subject.getName(),
                    subject.getCategory()
                );
                subjectInfos.add(info);
            }
        }
        response.setSubjects(subjectInfos);
        
        // Get availability
        List<TutorProfileResponse.AvailabilitySlot> availabilityList = new ArrayList<>();
        List<AvailabilitySlot> slots = availabilitySlotRepo.findByTutorIdAndIsActiveTrue(tutorId);
        for (AvailabilitySlot slot : slots) {
            TutorProfileResponse.AvailabilitySlot availSlot = 
                new TutorProfileResponse.AvailabilitySlot(
                    slot.getDayOfWeek(),
                    slot.getStartTime().toString(),
                    slot.getEndTime().toString()
                );
            availabilityList.add(availSlot);
        }
        response.setAvailability(availabilityList);
        
        // Reviews (empty for now - will add later)
        response.setReviews(new ArrayList<>());
        
        return response;
    }
}