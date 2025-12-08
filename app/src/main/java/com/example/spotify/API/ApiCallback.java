package com.example.spotify.API;

import com.example.spotify.models.Music;

import java.util.List;

public interface ApiCallback<T> {
    void onSuccess(List<T> Data);
    void onError(Throwable t);
}

