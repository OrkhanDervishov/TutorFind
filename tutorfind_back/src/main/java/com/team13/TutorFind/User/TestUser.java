package com.team13.TutorFind.User;

import jakarta.persistence.*;

@Entity
@Table(name = "testusers")
public class TestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // JPA requires a no-arg constructor
    public TestUser() {
    }

    public TestUser(String name){
        this.name = name;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
