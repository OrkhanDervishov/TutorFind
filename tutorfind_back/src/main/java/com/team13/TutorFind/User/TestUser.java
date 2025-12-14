package com.team13.TutorFind.User;

import jakarta.persistence.*;

@Entity
@Table(name="TestUsers")
public class TestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

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
