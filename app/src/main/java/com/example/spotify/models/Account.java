package com.example.spotify.models;

public class Account {
    private String TenTK, AVT, Email;

    public Account(String tenTK, String AVT, String email) {
        TenTK = tenTK;
        this.AVT = AVT;
        Email = email;
    }

    public String getTenTK() {
        return TenTK;
    }

    public void setTenTK(String tenTK) {
        TenTK = tenTK;
    }

    public String getAVT() {
        return AVT;
    }

    public void setAVT(String AVT) {
        this.AVT = AVT;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
