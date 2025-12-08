package com.example.spotify.Repository;

import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.UploadRequest;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import com.example.spotify.models.UploadState;

import java.util.Map;

public class CloudinaryUploadRepository {

    public CloudinaryUploadRepository() {
        // Constructor (Giả định MediaManager đã được khởi tạo trong Application class)
    }

    /**
     * Phương thức tải lên file, hỗ trợ phân biệt loại file (nhạc/ảnh).
     *
     * @param fileUri URI của file cần tải lên.
     * @param fileName Tên file gốc (ví dụ: song.mp3, cover.jpg).
     * @param fileType Loại file ("audio" hoặc "image") để cấu hình Cloudinary.
     * @param uploadState LiveData để cập nhật trạng thái tải lên.
     */
    public void uploadFile(Uri fileUri, String fileName, String fileType, MutableLiveData<UploadState> uploadState) {

        // 1. Chuẩn bị Public ID
        // Loại bỏ phần mở rộng file (.mp3, .jpg) để làm Public ID
        String publicId = fileName.substring(0, fileName.lastIndexOf('.'));

        // 2. Thiết lập cấu hình tải lên cơ bản
        UploadRequest request = MediaManager.get().upload(fileUri)
                // Dùng preset không ký (unsigned) đã tạo
                .unsigned("android_music_upload")
                .option("public_id", publicId);

        // 3. Cấu hình Tùy chỉnh dựa trên Loại File (RẤT QUAN TRỌNG)
        if ("image".equalsIgnoreCase(fileType)) {
            // Đối với ảnh bìa (cover image)
            request.option("resource_type", "image");
            // Thêm thư mục nếu cần
            request.option("folder", "spotify_covers");
        } else if ("audio".equalsIgnoreCase(fileType)) {
            // Đối với file nhạc (Cloudinary thường dùng resource_type là "video" cho audio)
            request.option("resource_type", "video");
            // Thêm thư mục nếu cần
            request.option("folder", "spotify_audio");
        }

        // 4. Thiết lập Callback và Gửi yêu cầu
        request.callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                // Xử lý bắt đầu tải lên (Thường được xử lý trong ViewModel)
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                // Cập nhật % tiến trình
                int progress = (int) (100.0 * bytes / totalBytes);
                uploadState.postValue(new UploadState(UploadState.PROGRESS, progress));
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                // Tải lên thành công, lấy URL bảo mật
                String secureUrl = (String) resultData.get("secure_url");
                
                // Gắn loại file vào URL trả về để Fragment dễ dàng phân biệt
                String successMessage = fileType + ":" + secureUrl;
                uploadState.postValue(new UploadState(UploadState.SUCCESS, successMessage));
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                // Báo cáo lỗi, gắn thêm loại file để Fragment biết lỗi xảy ra với loại file nào
                uploadState.postValue(new UploadState(UploadState.ERROR, "Cloudinary Error (" + fileType + "): " + error.getDescription()));
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                // Xử lý khi cần lên lịch lại
            }
        }).dispatch();
    }
}