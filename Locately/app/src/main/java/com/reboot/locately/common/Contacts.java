package com.reboot.locately.common;

public class Contacts {
    private String name,phone;
    private boolean selected=false;

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}