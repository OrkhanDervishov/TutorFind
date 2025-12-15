package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Services.NotificationService;
import com.team13.TutorFind.entity.Notification;
import com.team13.TutorFind.security.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    public NotificationController(NotificationService notificationService, JwtUtil jwtUtil) {
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader("Authorization") String authHeader,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            Page<Notification> notifications = notificationService.listForUser(userId, page, size);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<?> listUnread(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            List<Notification> notifications = notificationService.listUnread(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id) {
        try {
            Long userId = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            Notification notification = notificationService.markAsRead(id, userId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
            int updated = notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(Map.of("updated", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("message", message);
        return resp;
    }
}
