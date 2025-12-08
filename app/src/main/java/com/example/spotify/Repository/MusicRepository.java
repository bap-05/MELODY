package com.example.spotify.Repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.spotify.API.ApiClient;
import com.example.spotify.API.ApiResponse;
import com.example.spotify.API.IAPI;
import com.example.spotify.models.Music;
import com.example.spotify.models.Nghesi;
import com.example.spotify.models.Radio;
import com.example.spotify.models.UploadMusic;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicRepository {
    private final IAPI apiService;
    public int kt ;
    public MusicRepository() {
        apiService = ApiClient.getClient().create(IAPI.class);
    }
    private static List<Music> cachedMusic = null;
    private static List<Music> allMusic = null;
    public void getallmusic(MutableLiveData<List<Music>> liveData, MutableLiveData<String> err){
        Call<ApiResponse> call = apiService.getAllMusic();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                liveData.postValue(response.body().getGetallmusic());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                err.setValue(t.getMessage());
            }
        });
    }
    public void getmusicradio(MutableLiveData<List<Music>> liveData,MutableLiveData<String> error, Radio radio)
    {
        Call<ApiResponse> call = apiService.getMusicRadio(radio);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                liveData.postValue(response.body().getGetmusicradio());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                error.setValue(t.getMessage());
            }
        });
    }
    public void fetchMusic(MutableLiveData<List<Music>> liveData, MutableLiveData<String> error) {

        if (cachedMusic != null && !cachedMusic.isEmpty()) {
            liveData.postValue(cachedMusic);
            kt =1;
            return;
        }

        Call<ApiResponse> call = apiService.getMusic();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
//
                    List<Music> data = response.body().getGetmusic();
                    cachedMusic = data;
                    kt=0;
                    liveData.postValue(data);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                error.setValue(t.getMessage());
            }
        });
    }
    public void create(UploadMusic uploadMusic)
    {
        Call<Void> call = apiService.uploadMS(uploadMusic);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful())
                    Log.d("Upload","Lưu thành công");
                else
                    Log.d("Upload","Lưu thất bại");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR","Lỗi sever");
            }
        });
    }
    public void search(MutableLiveData<List<Music>>liveData, MutableLiveData<String> err, String query)
    {
        Call<ApiResponse> call = apiService.search_ms(query);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                liveData.postValue(response.body().getGetmusic());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                err.setValue(t.getMessage());
            }
        });
    }
}
