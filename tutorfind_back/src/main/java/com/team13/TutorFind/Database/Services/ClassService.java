package com.team13.TutorFind.Database.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team13.TutorFind.Database.Repos.AvailabilitySlotRepo;
import com.team13.TutorFind.Database.Repos.ClassRepo;
import com.team13.TutorFind.Database.Repos.EnrollmentRepo;
import com.team13.TutorFind.Database.Repos.SubjectRepo;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.dto.ClassResponse;
import com.team13.TutorFind.dto.CreateClassRequest;
import com.team13.TutorFind.entity.AvailabilitySlot;
import com.team13.TutorFind.entity.Class;

@Service
public class ClassService {
    
    @Autowired
    private ClassRepo classRepo;
    
    @Autowired
    private TutorDetailsRepo tutorDetailsRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private AvailabilitySlotRepo availabilitySlotRepo;
    
    @Autowired
    private SubjectRepo subjectRepo;
    
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    
    // Create class (tutors only)
    @Transactional
    public ClassResponse createClass(Long tutorId, CreateClassRequest request) {
        // Verify tutor exists
        TutorDetails tutor = tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        // Validate required fields
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new RuntimeException("Class name is required");
        }
        if (request.getAvailabilitySlotId() == null) {
            throw new RuntimeException("Availability slot is required for scheduling");
        }
        
        AvailabilitySlot slot = availabilitySlotRepo.findById(request.getAvailabilitySlotId())
            .orElseThrow(() -> new RuntimeException("Availability slot not found"));
        
        if (!slot.getTutorId().equals(tutorId)) {
            throw new RuntimeException("Slot does not belong to this tutor");
        }
        
        // Create class entity
        Class newClass = new Class();
        newClass.setTutorId(tutorId);
        newClass.setSubjectId(request.getSubjectId());
        newClass.setName(request.getName());
        newClass.setDescription(request.getDescription());
        newClass.setClassType(request.getClassType() != null ? request.getClassType() : "INDIVIDUAL");
        newClass.setMaxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 1);
        newClass.setCurrentStudents(0);
        newClass.setPricePerSession(request.getPricePerSession());
        newClass.setTotalSessions(request.getTotalSessions());
        newClass.setDurationMinutes(request.getDurationMinutes());
        newClass.setStartDate(request.getStartDate());
        newClass.setEndDate(request.getEndDate());
        newClass.setAvailabilitySlotId(request.getAvailabilitySlotId());
        newClass.setStatus("OPEN");
        
        Class savedClass = classRepo.save(newClass);
        
        return buildClassResponse(savedClass);
    }
    
    // Get all classes (public)
    public List<ClassResponse> getAllClasses() {
        return classRepo.findAll().stream()
            .map(this::buildClassResponse)
            .collect(Collectors.toList());
    }
    
    // Get open classes only
    public List<ClassResponse> getOpenClasses() {
        return classRepo.findByStatusOrderByCreatedAtDesc("OPEN").stream()
            .map(this::buildClassResponse)
            .collect(Collectors.toList());
    }
    
    // Get class by ID
    public ClassResponse getClassById(Long classId) {
        Class classEntity = classRepo.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        return buildClassResponse(classEntity);
    }
    
    // Get tutor's classes
    public List<ClassResponse> getTutorClasses(Long tutorId) {
        return classRepo.findByTutorId(tutorId).stream()
            .map(this::buildClassResponse)
            .collect(Collectors.toList());
    }
    
    // Update class
    @Transactional
    public ClassResponse updateClass(Long classId, Long tutorId, CreateClassRequest request) {
        Class classEntity = classRepo.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Verify ownership
        if (!classEntity.getTutorId().equals(tutorId)) {
            throw new RuntimeException("Unauthorized: You can only update your own classes");
        }
        
        // Update fields
        if (request.getName() != null) classEntity.setName(request.getName());
        if (request.getDescription() != null) classEntity.setDescription(request.getDescription());
        if (request.getClassType() != null) classEntity.setClassType(request.getClassType());
        if (request.getMaxStudents() != null) classEntity.setMaxStudents(request.getMaxStudents());
        if (request.getPricePerSession() != null) classEntity.setPricePerSession(request.getPricePerSession());
        if (request.getTotalSessions() != null) classEntity.setTotalSessions(request.getTotalSessions());
        if (request.getDurationMinutes() != null) classEntity.setDurationMinutes(request.getDurationMinutes());
        if (request.getStartDate() != null) classEntity.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) classEntity.setEndDate(request.getEndDate());
        if (request.getAvailabilitySlotId() != null) {
            AvailabilitySlot slot = availabilitySlotRepo.findById(request.getAvailabilitySlotId())
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));
            if (!slot.getTutorId().equals(tutorId)) {
                throw new RuntimeException("Slot does not belong to this tutor");
            }
            classEntity.setAvailabilitySlotId(request.getAvailabilitySlotId());
        }
        
        Class updated = classRepo.save(classEntity);
        return buildClassResponse(updated);
    }
    
    // Delete class
    @Transactional
    public void deleteClass(Long classId, Long tutorId) {
        Class classEntity = classRepo.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Verify ownership
        if (!classEntity.getTutorId().equals(tutorId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own classes");
        }
        
        // Check if class has enrollments
        Long enrollmentCount = enrollmentRepo.countByClassIdAndStatus(classId, "ACTIVE");
        if (enrollmentCount > 0) {
            throw new RuntimeException("Cannot delete class with active enrollments");
        }
        
        classRepo.delete(classEntity);
    }
    
    // Update class status
    @Transactional
    public void updateClassStatus(Long classId, String status) {
        Class classEntity = classRepo.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        
        classEntity.setStatus(status);
        classRepo.save(classEntity);
    }
    
    // Helper method to build response
    private ClassResponse buildClassResponse(Class classEntity) {
        ClassResponse response = new ClassResponse();
        response.setId(classEntity.getId());
        response.setTutorId(classEntity.getTutorId());
        response.setSubjectId(classEntity.getSubjectId());
        response.setName(classEntity.getName());
        response.setDescription(classEntity.getDescription());
        response.setClassType(classEntity.getClassType());
        response.setMaxStudents(classEntity.getMaxStudents());
        response.setCurrentStudents(classEntity.getCurrentStudents());
        int max = classEntity.getMaxStudents() != null ? classEntity.getMaxStudents() : 0;
        int current = classEntity.getCurrentStudents() != null ? classEntity.getCurrentStudents() : 0;
        response.setAvailableSeats(Math.max(0, max - current));
        response.setPricePerSession(classEntity.getPricePerSession());
        response.setTotalSessions(classEntity.getTotalSessions());
        response.setDurationMinutes(classEntity.getDurationMinutes());
        response.setStatus(classEntity.getStatus());
        response.setStartDate(classEntity.getStartDate());
        response.setEndDate(classEntity.getEndDate());
        response.setCreatedAt(classEntity.getCreatedAt());
        response.setAvailabilitySlotId(classEntity.getAvailabilitySlotId());
        
        // Get tutor name
        tutorDetailsRepo.findById(classEntity.getTutorId()).ifPresent(tutor -> {
            userRepo.findById(tutor.getUserId()).ifPresent(user -> {
                response.setTutorName(user.getFirstName() + " " + user.getLastName());
            });
        });
        
        // Get subject name
        if (classEntity.getSubjectId() != null) {
            subjectRepo.findById(classEntity.getSubjectId()).ifPresent(subject -> {
                response.setSubjectName(subject.getName());
            });
        }
        
        // Attach availability slot info + derived schedule fields
        if (classEntity.getAvailabilitySlotId() != null) {
            availabilitySlotRepo.findById(classEntity.getAvailabilitySlotId()).ifPresent(slot -> {
                ClassResponse.AvailabilitySlotInfo slotInfo = new ClassResponse.AvailabilitySlotInfo();
                slotInfo.setId(slot.getId());
                slotInfo.setDayOfWeek(slot.getDayOfWeek());
                slotInfo.setStartTime(slot.getStartTime() != null ? slot.getStartTime().toString() : null);
                slotInfo.setEndTime(slot.getEndTime() != null ? slot.getEndTime().toString() : null);
                response.setAvailabilitySlot(slotInfo);
                response.setScheduleDay(slot.getDayOfWeek());
                if (slot.getStartTime() != null && slot.getEndTime() != null) {
                    response.setScheduleTime(slot.getStartTime() + "-" + slot.getEndTime());
                }
            });
        }
        
        return response;
    }
}
