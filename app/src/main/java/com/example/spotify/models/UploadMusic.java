package com.example.spotify.models;

import java.util.List;

public class UploadMusic {
    private List<Nghesi> nghesiList;
    private String anhUrl,tenBH;
    private String musicUrl;

    public UploadMusic(List<Nghesi> nghesiList, String anhUrl, String tenBH, String musicUrl) {
        this.nghesiList = nghesiList;
        this.anhUrl = anhUrl;
        this.tenBH = tenBH;
        this.musicUrl = musicUrl;
    }
}
