package com.example.spotify.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.spotify.API.ApiClient;
import com.example.spotify.API.ApiResponse;
import com.example.spotify.API.IAPI;
import com.example.spotify.models.DSPhat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DSPhatRepository {
    private final IAPI iapi;

    public DSPhatRepository() {
        this.iapi = ApiClient.getClient().create(IAPI.class);
    }
    public void DSPhat(MutableLiveData<List<DSPhat>>liveData, MutableLiveData<String>err, String ma){
        Call<ApiResponse> call = iapi.dsphat(ma);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body().getDsPhatList()!=null)
                {
                    liveData.postValue(response.body().getDsPhatList());
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Lỗi API",t.getMessage(),t);
                err.setValue(t.getMessage());
            }
        });
    }
}
