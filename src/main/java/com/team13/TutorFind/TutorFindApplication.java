package com.team13.TutorFind;

import com.team13.TutorFind.Rest.TestUserController;
import com.team13.TutorFind.User.TestUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TutorFindApplication {

	public static void main(String[] args) {

        SpringApplication.run(TutorFindApplication.class, args);

        Test();
	}

    public static void Test(){
        TestUser user = new TestUser("Batman");
    }
}
