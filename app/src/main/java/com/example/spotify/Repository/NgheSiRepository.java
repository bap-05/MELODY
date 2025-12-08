package com.example.spotify.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.spotify.API.ApiClient;
import com.example.spotify.API.ApiResponse;
import com.example.spotify.API.IAPI;
import com.example.spotify.models.Music;
import com.example.spotify.models.Nghesi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NgheSiRepository {
    private final IAPI iapi;
    private List<Nghesi> nghesiList = null;

    public NgheSiRepository() {
        iapi = ApiClient.getClient().create(IAPI.class);
    }
    public void getNS(MutableLiveData<List<Nghesi>> liveData, MutableLiveData<String> err){
        if(nghesiList !=null && !nghesiList.isEmpty())
        {
            liveData.postValue(nghesiList);
            return;
        }
        Call<ApiResponse> call = iapi.getNgheSi();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                nghesiList = response.body().getNghesiList();
                liveData.postValue(nghesiList);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                err.setValue(t.getMessage());
            }
        });
    }
    public void getms(MutableLiveData<List<Music>> liveData, MutableLiveData<String> err, Nghesi ns){
        Call<ApiResponse> call = iapi.getMS_NS(ns);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                liveData.postValue(response.body().getGetmusic());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR",t.getMessage(), t);
                err.setValue(t.getMessage());
            }
        });
    }
    public void search_ns (MutableLiveData<List<Nghesi>> liveData, MutableLiveData<String>err, String query){
        Call<ApiResponse>call = iapi.search_ns(query);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                liveData.postValue(response.body().getNghesiList());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR",t.getMessage(), t);
                err.setValue(t.getMessage());
            }
        });
    }
    public void themNS(Nghesi ns){
        Call<Void> call = iapi.ThemNS(ns);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful())
                    Log.d("ThemNS","Them Thanh Cong");
                else
                    Log.d("ThemNS","Them That Bai");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR","Lỗi server");
            }
        });
    }
}
