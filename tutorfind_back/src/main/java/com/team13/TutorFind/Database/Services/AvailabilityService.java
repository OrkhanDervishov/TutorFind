package com.team13.TutorFind.Database.Services;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team13.TutorFind.Database.Repos.AvailabilitySlotRepo;
import com.team13.TutorFind.Database.Repos.DistrictRepo;
import com.team13.TutorFind.Database.Repos.SubjectRepo;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.TutorDistrictRepo;
import com.team13.TutorFind.Database.Repos.TutorSubjectRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.entity.AvailabilitySlot;
import com.team13.TutorFind.entity.TutorDistrict;
import com.team13.TutorFind.entity.TutorSubject;
import java.util.Locale;
import java.util.Set;

@Service
public class AvailabilityService {
    
    private final AvailabilitySlotRepo availabilitySlotRepo;
    private final TutorSubjectRepo tutorSubjectRepo;
    private final TutorDistrictRepo tutorDistrictRepo;
    private final TutorDetailsRepo tutorDetailsRepo;
    private final SubjectRepo subjectRepo;
    private final DistrictRepo districtRepo;
    private static final Set<String> VALID_DAYS = Set.of(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    );
    
    public AvailabilityService(
            AvailabilitySlotRepo availabilitySlotRepo,
            TutorSubjectRepo tutorSubjectRepo,
            TutorDistrictRepo tutorDistrictRepo,
            TutorDetailsRepo tutorDetailsRepo,
            SubjectRepo subjectRepo,
            DistrictRepo districtRepo) {
        this.availabilitySlotRepo = availabilitySlotRepo;
        this.tutorSubjectRepo = tutorSubjectRepo;
        this.tutorDistrictRepo = tutorDistrictRepo;
        this.tutorDetailsRepo = tutorDetailsRepo;
        this.subjectRepo = subjectRepo;
        this.districtRepo = districtRepo;
    }
    
    // ==================== AVAILABILITY SLOTS ====================
    
    @Transactional
    public Map<String, Object> addAvailabilitySlot(Long tutorId, String dayOfWeek, 
                                                     String startTime, String endTime) {
        // Verify tutor exists
        TutorDetails tutor = tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor profile not found"));

        String normalizedDay = normalizeDay(dayOfWeek);
        if (!VALID_DAYS.contains(normalizedDay)) {
            throw new RuntimeException("Invalid day of week");
        }
        
        // Create new slot
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setTutorId(tutorId);
        slot.setDayOfWeek(normalizedDay);
        slot.setStartTime(LocalTime.parse(startTime));
        slot.setEndTime(LocalTime.parse(endTime));
        slot.setIsActive(true);
        
        AvailabilitySlot saved = availabilitySlotRepo.save(slot);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Availability slot added successfully");
        response.put("slotId", saved.getId());
        return response;
    }
    private String normalizeDay(String dayOfWeek) {
        if (dayOfWeek == null) {
            return null;
        }
        return dayOfWeek.trim().toUpperCase(Locale.ROOT);
    }
    public List<AvailabilitySlot> getTutorAvailabilitySlots(Long tutorId) {
    // Verify tutor exists
    tutorDetailsRepo.findById(tutorId)
        .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
    
    // Return all slots for this tutor
    return availabilitySlotRepo.findByTutorId(tutorId);
}
    @Transactional
    public Map<String, Object> removeAvailabilitySlot(Long tutorId, Long slotId) {
        // Get the slot
        AvailabilitySlot slot = availabilitySlotRepo.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Availability slot not found"));
        
        // Verify ownership
        if (!slot.getTutorId().equals(tutorId)) {
            throw new RuntimeException("Unauthorized: This slot does not belong to you");
        }
        
        availabilitySlotRepo.delete(slot);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Availability slot removed successfully");
        return response;
    }
    
    // ==================== SUBJECTS ====================
    
    @Transactional
    public Map<String, Object> addSubject(Long tutorId, Long subjectId) {
        // Verify tutor exists
        tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
        
        // Verify subject exists
        subjectRepo.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found"));
        
        // Check if already exists
        if (tutorSubjectRepo.findByTutorId(tutorId).stream()
                .anyMatch(ts -> ts.getSubjectId().equals(subjectId))) {
            throw new RuntimeException("Subject already added to your profile");
        }
        
        // Create new tutor-subject relationship
        TutorSubject tutorSubject = new TutorSubject();
        tutorSubject.setTutorId(tutorId);
        tutorSubject.setSubjectId(subjectId);
        
        TutorSubject saved = tutorSubjectRepo.save(tutorSubject);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Subject added successfully");
        response.put("id", saved.getId());
        return response;
    }
    
    @Transactional
    public Map<String, Object> removeSubject(Long tutorId, Long subjectId) {
        // Find the tutor-subject relationship
        TutorSubject tutorSubject = tutorSubjectRepo.findByTutorId(tutorId).stream()
            .filter(ts -> ts.getSubjectId().equals(subjectId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Subject not found in your profile"));
        
        tutorSubjectRepo.delete(tutorSubject);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Subject removed successfully");
        return response;
    }
    
    // ==================== DISTRICTS ====================
    
    @Transactional
    public Map<String, Object> addDistrict(Long tutorId, Long districtId) {
        // Verify tutor exists
        tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
        
        // Verify district exists
        districtRepo.findById(districtId)
            .orElseThrow(() -> new RuntimeException("District not found"));
        
        // Check if already exists
        if (tutorDistrictRepo.findByTutorId(tutorId).stream()
                .anyMatch(td -> td.getDistrictId().equals(districtId))) {
            throw new RuntimeException("District already added to your profile");
        }
        
        // Create new tutor-district relationship
        TutorDistrict tutorDistrict = new TutorDistrict();
        tutorDistrict.setTutorId(tutorId);
        tutorDistrict.setDistrictId(districtId);
        
        TutorDistrict saved = tutorDistrictRepo.save(tutorDistrict);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "District added successfully");
        response.put("id", saved.getId());
        return response;
    }
    
    @Transactional
    public Map<String, Object> removeDistrict(Long tutorId, Long districtId) {
        // Find the tutor-district relationship
        TutorDistrict tutorDistrict = tutorDistrictRepo.findByTutorId(tutorId).stream()
            .filter(td -> td.getDistrictId().equals(districtId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("District not found in your profile"));
        
        tutorDistrictRepo.delete(tutorDistrict);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "District removed successfully");
        return response;
    }
}
