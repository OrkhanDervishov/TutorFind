package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.FlagRepo;
import com.team13.TutorFind.entity.Flag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FlagService {

    private final FlagRepo flagRepo;

    public FlagService(FlagRepo flagRepo) {
        this.flagRepo = flagRepo;
    }

    public Flag createFlag(Long userId, String contentType, Long contentId, String reason) {
        Flag flag = new Flag(userId, contentType, contentId, reason);
        return flagRepo.save(flag);
    }

    public Page<Flag> listFlags(String status, String contentType, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        if (status != null && contentType != null) {
            return flagRepo.findByContentTypeIgnoreCaseAndStatusIgnoreCase(contentType, status, pageable);
        } else if (status != null) {
            return flagRepo.findByStatusIgnoreCase(status, pageable);
        } else if (contentType != null) {
            return flagRepo.findByContentTypeIgnoreCase(contentType, pageable);
        }
        return flagRepo.findAll(pageable);
    }

    public Flag updateStatus(Long flagId, String status) {
        Flag flag = flagRepo.findById(flagId)
                .orElseThrow(() -> new RuntimeException("Flag not found"));
        flag.setStatus(status);
        return flagRepo.save(flag);
    }
}
