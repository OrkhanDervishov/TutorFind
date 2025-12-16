package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.NotificationRepo;
import com.team13.TutorFind.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepo notificationRepo;

    public NotificationService(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public Notification create(Long userId, String type, String payloadJson) {
        Notification notification = new Notification(userId, type, payloadJson);
        return notificationRepo.save(notification);
    }

    public List<Notification> listForUser(Long userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Page<Notification> listForUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<Notification> listUnread(Long userId) {
        return notificationRepo.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public Notification markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setIsRead(true);
        return notificationRepo.save(notification);
    }

    public int markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepo.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepo.saveAll(unread);
        return unread.size();
    }
}
