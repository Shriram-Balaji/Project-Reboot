package com.reboot.locately.common;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Users {




    private String first_name,last_name,latitude,longitude,battery_percent,phone_number;



    public Users(){
    }

    public Users(String first_name, String last_name, String latitude, String longitude, String battery_percent, String phone_number) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.battery_percent = battery_percent;
        this.phone_number = phone_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBattery_percent() {
        return battery_percent;
    }

    public void setBattery_percent(String battery_percent) {
        this.battery_percent = battery_percent;
    }


}
