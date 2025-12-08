package com.example.spotify.models;

public class Nghesi {
    private String TenNgheSi, AVT;
    private int MaNgheSi;

    public Nghesi(String tenNgheSi, String AVT, int maNgheSi) {
        TenNgheSi = tenNgheSi;
        this.AVT = AVT;
        MaNgheSi = maNgheSi;
    }

    public Nghesi(String tenNgheSi, String AVT) {
        TenNgheSi = tenNgheSi;
        this.AVT = AVT;
    }

    public String getTenNgheSi() {
        return TenNgheSi;
    }

    public void setTenNgheSi(String tenNgheSi) {
        TenNgheSi = tenNgheSi;
    }

    public String getAVT() {
        return AVT;
    }

    public void setAVT(String AVT) {
        this.AVT = AVT;
    }

    public int getMaNgheSi() {
        return MaNgheSi;
    }

    public void setMaNgheSi(int maNgheSi) {
        MaNgheSi = maNgheSi;
    }
}
