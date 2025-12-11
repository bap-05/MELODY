package com.example.spotify.API;

import androidx.lifecycle.MutableLiveData;

import com.cloudinary.Api;
import com.example.spotify.models.Account;
import com.example.spotify.models.Nghesi;
import com.example.spotify.models.Radio;
import com.example.spotify.models.UploadMusic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IAPI {
    @GET("/music/3")
    Call<ApiResponse> getMusic();
    @GET("/radio/")
    Call<ApiResponse> getRadio();
    @GET("/music/getallmusic")
    Call<ApiResponse> getAllMusic();
    @POST("/account/savetk")
    Call<Void> saveTK(@Body MutableLiveData<Account> account);
    @POST("music/getmusic")
    Call<ApiResponse> getMusicRadio(@Body Radio radio);
    @GET("nghesi/5")
    Call<ApiResponse> getNgheSi();
    @POST("nghesi/music")
    Call<ApiResponse> getMS_NS (@Body Nghesi nghesi);
    @GET("nghesi/search")
    Call<ApiResponse> search_ns(@Query("q")String query);
    @POST("music/create")
    Call<Void> uploadMS(@Body UploadMusic uploadMusic);
    @POST("nghesi/insert")
    Call<Void> ThemNS(@Body Nghesi nghesi);
    @GET("music/search")
    Call<ApiResponse> search_ms(@Query("q")String query);
    @GET("DSPhat/{ma}")
    Call<ApiResponse> dsphat(@Path("ma") String ma);
}
