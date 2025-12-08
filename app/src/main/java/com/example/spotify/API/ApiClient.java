package com.example.spotify.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String url = "https://bappp-api-melody.hf.space/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient(){
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
