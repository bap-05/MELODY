package com.example.spotify.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.spotify.API.ApiClient;
import com.example.spotify.API.ApiResponse;
import com.example.spotify.API.IAPI;
import com.example.spotify.models.Radio;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RadioRepository {
    private final IAPI api;

    public RadioRepository() {
        api = ApiClient.getClient().create(IAPI.class);
    }
    public void top5Radio(MutableLiveData<List<Radio>> liveData, MutableLiveData<String>error)
    {
        Call<ApiResponse> call = api.getRadio();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body()!=null){
                    liveData.setValue(response.body().getGetradio());
                }
                else
                    error.setValue("Response null or failed");
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(),t);
                error.setValue(t.getMessage());
            }
        });
    }
}
