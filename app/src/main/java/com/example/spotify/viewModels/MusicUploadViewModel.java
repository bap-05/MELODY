package com.example.spotify.viewModels;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.spotify.models.UploadState;
import com.example.spotify.Repository.CloudinaryUploadRepository;

public class MusicUploadViewModel extends ViewModel {
    private final CloudinaryUploadRepository repository;
    // Dùng chung LiveData cho cả trạng thái tải nhạc và ảnh
    private final MutableLiveData<UploadState> uploadState;

    public MusicUploadViewModel() {
        repository = new CloudinaryUploadRepository();
        uploadState = new MutableLiveData<>(new UploadState(UploadState.IDLE, "Ready"));
    }

    public LiveData<UploadState> getUploadState() {
        return uploadState;
    }

    /**
     * Tải lên file nhạc.
     */
    public void startUpload(Uri fileUri, String fileName) {
        uploadState.setValue(new UploadState(UploadState.PROGRESS, 0));
        // GỌI REPOSITORY: Thêm tham số loại file (ví dụ: "audio")
        repository.uploadFile(fileUri, fileName, "audio", uploadState);
    }

    /**
     * Tải lên file ảnh (ảnh bìa).
     */
    public void startImageUpload(Uri imageUri, String fileName) {
        // Đặt trạng thái ban đầu (Có thể dùng một đối tượng UploadState riêng cho ảnh nếu muốn tách biệt)
        uploadState.setValue(new UploadState(UploadState.PROGRESS, 0));
        // GỌI REPOSITORY: Thêm tham số loại file (ví dụ: "image")
        repository.uploadFile(imageUri, fileName, "image", uploadState);
    }
}