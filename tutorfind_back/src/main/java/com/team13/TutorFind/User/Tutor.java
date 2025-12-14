package com.team13.TutorFind.User;


public class Tutor extends User{
    private float rating;
    private String bio;

    Tutor(){
       super();
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
