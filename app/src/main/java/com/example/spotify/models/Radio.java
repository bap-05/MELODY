package com.example.spotify.models;

public class Radio {
    private String  TenNgheSi, TenRadio, Anh1,Anh2,Anh3;
    private int MaRadio;
    public int getMaRadio() {
        return MaRadio;
    }

    public void setMaRadio(int maRadio) {
        MaRadio = maRadio;
    }

    public Radio(String tenNgheSi, String tenRadio, String anh1, String anh2, String anh3) {
        TenNgheSi = tenNgheSi;
        TenRadio = tenRadio;
        Anh1 = anh1;
        Anh2 = anh2;
        Anh3 = anh3;
    }

    public String getTenNgheSi() {
        return TenNgheSi;
    }

    public void setTenNgheSi(String tenNgheSi) {
        TenNgheSi = tenNgheSi;
    }

    public String getTenRadio() {
        return TenRadio;
    }

    public void setTenRadio(String tenRadio) {
        TenRadio = tenRadio;
    }

    public String getAnh1() {
        return Anh1;
    }

    public void setAnh1(String anh1) {
        Anh1 = anh1;
    }

    public String getAnh2() {
        return Anh2;
    }

    public void setAnh2(String anh2) {
        Anh2 = anh2;
    }

    public String getAnh3() {
        return Anh3;
    }

    public void setAnh3(String anh3) {
        Anh3 = anh3;
    }
}
