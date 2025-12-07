package com.team13.TutorFind.User;

import jakarta.persistence.*;

@Entity
@Table(name = "tutor_details")
public class TutorDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float rating;
    private String bio;

    public TutorDetails(){}


    public Long getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating){
        if(rating < 1 || rating > 5) return;
        this.rating = rating;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        if(bio.length() > 1000){
            return;
        }
        this.bio = bio;
    }
}
