package com.example.spotify.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.spotify.API.ApiClient;
import com.example.spotify.API.IAPI;
import com.example.spotify.models.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {
    private final IAPI iapi;

    public AccountRepository() {
        iapi = ApiClient.getClient().create(IAPI.class);
    }
    public void savetk(MutableLiveData<Account> account){
        Call<Void> call = iapi.saveTK(account);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful())
                    Log.d("savetk","Gửi thành công");
                else
                    Log.d("savetk","Gửi Thất bại");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR","Lỗi sever");
            }
        });
    }
}
