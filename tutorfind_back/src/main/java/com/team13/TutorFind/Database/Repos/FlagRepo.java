package com.team13.TutorFind.Database.Repos;

import com.team13.TutorFind.entity.Flag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlagRepo extends JpaRepository<Flag, Long> {
    Page<Flag> findByStatusIgnoreCase(String status, Pageable pageable);
    Page<Flag> findByContentTypeIgnoreCase(String contentType, Pageable pageable);
    Page<Flag> findByContentTypeIgnoreCaseAndStatusIgnoreCase(String contentType, String status, Pageable pageable);
}
