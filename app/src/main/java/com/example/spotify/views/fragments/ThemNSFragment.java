package com.example.spotify.views.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.spotify.R;
import com.example.spotify.models.Nghesi;
import com.example.spotify.models.UploadState;
import com.example.spotify.viewModels.MusicUploadViewModel;
import com.example.spotify.viewModels.NghesiViewModel;
import com.example.spotify.views.MainActivity;
import com.squareup.picasso.Picasso;


public class ThemNSFragment extends Fragment {
    private ProgressBar progressBar;
    private ImageView img_avt;
    private MusicUploadViewModel viewModel;
    private Button btn_chonAnh, btn_them;
    private ActivityResultLauncher<String> filePickerLauncher;
    private Uri selectedUri = null;
    private String uploadedImageUrl = null;
    private EditText txt_tenNS;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_them_n_s, container, false);
        addView(v);
        initFilePickerLauncher();
        btn_chonAnh.setOnClickListener(view->{
            openFilePicker();
        });
        btn_them.setOnClickListener(view -> {
            if (selectedUri == null||txt_tenNS.getText().toString().isEmpty()) {
                Toast.makeText(((MainActivity)requireActivity()), "Thêm đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            startImageUploadProcess();
        });
        viewModel.getUploadState().observe(getViewLifecycleOwner(), this::handleUploadState);
        return v;
    }

    private void handleUploadState(UploadState uploadState) {
        switch (uploadState.getStatus()){
            case UploadState.IDLE:
                progressBar.setVisibility(View.GONE);
                btn_chonAnh.setEnabled(true);
                break;
            case UploadState.PROGRESS:
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(uploadState.getPercentage());
                btn_chonAnh.setEnabled(false);
                break;
            case UploadState.SUCCESS:
                String result = uploadState.getMessageOrUrl();
                if (result != null && result.contains(":")) {
                    String[] parts = result.split(":", 2);
                    String fileType = parts[0];
                    String secureUrl = parts[1];

                    if ("image".equalsIgnoreCase(fileType)) {
                        // XỬ LÝ KẾT QUẢ TẢI ẢNH
                        // Lưu URL ảnh để sử dụng sau (ví dụ: gửi lên DB cùng thông tin bài hát)
                        // (Bạn cần thêm một biến private String imageUrl;)
                        uploadedImageUrl = secureUrl;
                        selectedUri = null; // Xóa Uri đã chọn
                        progressBar.setVisibility(View.GONE);
                        btn_chonAnh.setEnabled(true);
                        Toast.makeText(getContext(), "Tải ảnh bìa Cloudinary thành công!", Toast.LENGTH_LONG).show();
                    }
                    checkIfReadyToInsert();
                }
                break;
            case UploadState.ERROR:
                // Xử lý lỗi: Cho phép người dùng thử lại
                progressBar.setVisibility(View.GONE);
                btn_chonAnh.setEnabled(true);

                // Cập nhật thông báo lỗi, giả định lỗi có thể xảy ra cho cả 2 loại file
                String errorMsg = uploadState.getMessageOrUrl();
                if (errorMsg != null && errorMsg.contains("(image)")) {

                    Toast.makeText(getContext(), "Lỗi tải ảnh bìa!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi tải file nhạc!", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    private void checkIfReadyToInsert() {
        if(uploadedImageUrl !=null && !txt_tenNS.getText().toString().isEmpty())
        {
            Nghesi ns = new Nghesi(txt_tenNS.getText().toString().trim(),uploadedImageUrl);
            NghesiViewModel nghesiViewModel = new ViewModelProvider(requireActivity()).get(NghesiViewModel.class);
            nghesiViewModel.themNS(ns);
            txt_tenNS.setText("");
            uploadedImageUrl = null;
            img_avt.setImageResource(0);
            Toast.makeText(getContext(), "Nghệ sĩ đã được thêm thành công vào Server!", Toast.LENGTH_LONG).show();
        }
    }

    private void initFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri ->{
                    if(uri != null){
                        selectedUri = uri;
                        Picasso.get().load(uri).into(img_avt);
                        Toast.makeText(getContext(), "Đã chọn file ảnh", Toast.LENGTH_SHORT).show();
                    }else
                        selectedUri = null;
                }
        );
    }

    private void addView(View v) {
        img_avt = v.findViewById(R.id.img_avt_themNS);
        btn_them = v.findViewById(R.id.btn_them_themNS);
        txt_tenNS = v.findViewById(R.id.txt_ten_themNS);
        btn_chonAnh = v.findViewById(R.id.btn_chonanh_themNS);
        progressBar = v.findViewById(R.id.progress_bar_themNS);
        viewModel = new ViewModelProvider(requireActivity()).get(MusicUploadViewModel.class);
    }
    private void openFilePicker(){
        filePickerLauncher.launch("image/*");
        Toast.makeText(getContext(), "Vui lòng chọn file ảnh (JPEG/PNG)...", Toast.LENGTH_SHORT).show();
    }
    private void startImageUploadProcess() {
        if (selectedUri != null) {
            // Tên file ảnh nên có tiền tố khác để dễ phân biệt với nhạc
            String fileName = System.currentTimeMillis() + ".jpg";

            // GIẢ ĐỊNH: Chúng ta sử dụng một phương thức mới trong ViewModel để tải ảnh lên,
            // vì trạng thái tải lên ảnh và nhạc thường là khác nhau.
            // Nếu bạn dùng chung ViewModel, bạn cần sửa đổi logic trong ViewModel.
            viewModel.startImageUpload(selectedUri, fileName); // Cần thêm phương thức này vào MusicUploadViewModel
            btn_chonAnh.setEnabled(false);
        }
    }
}