package com.example.spotify.API;

import com.example.spotify.models.Music;
import com.example.spotify.models.Nghesi;
import com.example.spotify.models.Radio;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("getallmusic")
    private List<Music> getallmusic;
    @SerializedName("getmusic")
    private List<Music> getmusic;
    @SerializedName("getmusicradio")
    private List<Music> getmusicradio;
    @SerializedName("getradio")
    private List<Radio> getradio;
    @SerializedName("getNS")
    public List<Nghesi> nghesiList;

    public List<Nghesi> getNghesiList() {
        return nghesiList;
    }

    public List<Music> getGetmusic() {
        return getmusic;
    }

    public List<Radio> getGetradio() {
        return getradio;
    }

    public List<Music> getGetallmusic() {
        return getallmusic;
    }

    public List<Music> getGetmusicradio() {
        return getmusicradio;
    }
}
