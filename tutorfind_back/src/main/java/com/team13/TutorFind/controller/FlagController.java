package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Services.FlagService;
import com.team13.TutorFind.entity.Flag;
import com.team13.TutorFind.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/flags")
@CrossOrigin(origins = "*")
public class FlagController {

    private final FlagService flagService;
    private final JwtUtil jwtUtil;

    public FlagController(FlagService flagService, JwtUtil jwtUtil) {
        this.flagService = flagService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> createFlag(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody Map<String, Object> body) {
        try {
            Long userId = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            String contentType = (String) body.get("contentType");
            Number cid = (Number) body.get("contentId");
            String reason = (String) body.get("reason");
            if (contentType == null || cid == null) {
                throw new RuntimeException("contentType and contentId are required");
            }
            Flag flag = flagService.createFlag(userId, contentType, cid.longValue(), reason);
            return ResponseEntity.ok(flag);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
