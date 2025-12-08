package com.example.spotify.models;

// UploadState.java

public class UploadState {
    // Constants cho loại trạng thái
    public static final int IDLE = 0;
    public static final int PROGRESS = 1;
    public static final int SUCCESS = 2;
    public static final int ERROR = 3;

    private final int status;
    private final String messageOrUrl; // Chứa URL nếu SUCCESS, chứa thông báo lỗi nếu ERROR
    private final int percentage; // Chứa % tiến trình nếu PROGRESS

    // Constructor cho SUCCESS/ERROR/IDLE
    public UploadState(int status, String messageOrUrl) {
        this.status = status;
        this.messageOrUrl = messageOrUrl;
        this.percentage = 0;
    }

    // Constructor cho PROGRESS
    public UploadState(int status, int percentage) {
        this.status = status;
        this.percentage = percentage;
        this.messageOrUrl = null;
    }

    // Getters
    public int getStatus() { return status; }
    public String getMessageOrUrl() { return messageOrUrl; }
    public int getPercentage() { return percentage; }
}