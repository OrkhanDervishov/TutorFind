package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Repos.SubjectRepo;
import com.team13.TutorFind.entity.Subject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/subjects")
public class SubjectController {
    
    private final SubjectRepo subjectRepo;
    
    public SubjectController(SubjectRepo subjectRepo) {
        this.subjectRepo = subjectRepo;
    }
    
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectRepo.findAll();
        return ResponseEntity.ok(subjects);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, List<Subject>>> getSubjectsByCategory() {
        List<Subject> allSubjects = subjectRepo.findAll();
        
        // Group by category
        Map<String, List<Subject>> grouped = allSubjects.stream()
            .collect(Collectors.groupingBy(Subject::getCategory));
        
        return ResponseEntity.ok(grouped);
    }
}