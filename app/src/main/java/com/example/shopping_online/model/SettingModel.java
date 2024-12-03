package com.example.shopping_online.model;

import java.io.Serializable;

public class SettingModel implements Serializable {
    private String id, nameuser, phonenumber, username,password, uid;
    private boolean gender;

    public SettingModel() {
    }

    public SettingModel(boolean gender, String id, String nameuser, String password, String phonenumber, String username) {
        this.gender = gender;
        this.id = id;
        this.nameuser = nameuser;
        this.password = password;
        this.phonenumber = phonenumber;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameuser() {
        return nameuser;
    }

    public void setNameuser(String nameuser) {
        this.nameuser = nameuser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
