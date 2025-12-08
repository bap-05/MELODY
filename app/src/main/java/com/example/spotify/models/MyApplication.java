package com.example.spotify.models;


import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {

    // THAY THẾ CHUỖI NÀY BẰNG TÊN CLOUD NAME THỰC TẾ CỦA BẠN
    private static final String CLOUD_NAME = "dkaids4dv";

    @Override
    public void onCreate() {
        super.onCreate();

        // Khởi tạo cấu hình Cloudinary
        Map config = new HashMap();
        config.put("cloud_name", CLOUD_NAME);

        try {
            MediaManager.init(this, config);
        } catch (IllegalStateException e) {
            // Đã khởi tạo
        }
    }
}