package com.team13.TutorFind.Database;

import com.team13.TutorFind.User.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepo extends JpaRepository<TestUser, Long> {

}
