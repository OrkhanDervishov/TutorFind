package com.team13.TutorFind.User;


public class Tutor extends User{
   String bio;

   Tutor(){
       super();
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
