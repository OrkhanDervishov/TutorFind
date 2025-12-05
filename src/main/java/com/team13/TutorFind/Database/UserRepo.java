package com.team13.TutorFind.Database;

import com.team13.TutorFind.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
