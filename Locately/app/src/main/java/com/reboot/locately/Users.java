package com.reboot.locately;


public class Users {
    private String password,profileName;


    //This style of declaring objects for manipulating database is called a POJO.
    //POJOs are used by Firebase for interacting with the Key-Value pairs.

    private double lattitde,longitde;

    public Users(String password, String profileName){
        this.password=password;
        this.profileName=profileName;
    }
    public void setPassword(String password){
        this.password=password;
    }

    public void setProfileName(String profileName){
        this.profileName=profileName;
    }

    public String getPassword(){
        return this.password;
    }

    public String getProfileName(){
        return this.profileName;
    }
}