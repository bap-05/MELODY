package com.example.spotify.views.fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify.R;
import com.example.spotify.adapter.NgheSi2Adapter;
import com.example.spotify.adapter.NgheSi_1Adapter;
import com.example.spotify.models.Nghesi;
import com.example.spotify.models.UploadMusic;
import com.example.spotify.models.UploadState; // Đảm bảo lớp này đúng đường dẫn
import com.example.spotify.viewModels.MusicUploadViewModel; // Đảm bảo lớp này đúng đường dẫn
import com.example.spotify.viewModels.MusicViewModel;
import com.example.spotify.viewModels.NghesiViewModel;
import com.example.spotify.views.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UploadMusicFragment extends Fragment{

    private MusicUploadViewModel viewModel;
    private Button uploadButton, btn_upanh, btn_upload;
    private ProgressBar progressBar;
    private EditText txt_query,txt_tenBH;
    private NgheSi_1Adapter ngheSi1Adapter=new NgheSi_1Adapter(new ArrayList<>());
    private List<Nghesi> nghesiList = new ArrayList<>();
    private ImageView btn_tk, img_upload;
    private RecyclerView rcv_tkns, rcv_ns;
    // Khai báo ActivityResultLauncher và Uri đã chọn
    private ActivityResultLauncher<String> filePickerLauncher;
    private Uri selectedMusicUri = null;
    private NgheSi2Adapter ngheSi2Adapter;
    private final long DEBOUNCE_DELAY_MS = 500; // Độ trễ 500ms
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private String uploadedMusicUrl = null; // <-- THÊM BIẾN NÀY
    private String uploadedImageUrl = null;
    private TextView txt_linkBH;
    private ActivityResultLauncher<String> imageFilePickerLauncher;
    private Uri selectedImageUri = null; // URI của ảnh bìa đã chọn
    private Button btnUploadImage; // Giả định nút upload ảnh riêng biệt
    // =================================================================
    // 1. LIFECYCLE VÀ KHỞI TẠO VIEW
    // =================================================================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        uploadButton = view.findViewById(R.id.upload_button);
        btn_upload = view.findViewById(R.id.btn_upload);
        btn_upanh = view.findViewById(R.id.upload_button_anh);
        img_upload = view.findViewById(R.id.img_upload);
        txt_tenBH = view.findViewById(R.id.txt_upload_tenBH);
        progressBar = view.findViewById(R.id.progress_bar);
        txt_query = view.findViewById(R.id.txt_upload_ns);
        txt_linkBH = view.findViewById(R.id.txt_linkBH);
        rcv_ns = view.findViewById(R.id.rcv_upload_tenns);
        rcv_tkns = view.findViewById(R.id.rcv_upload_ns);
        rcv_tkns.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_ns.setLayoutManager(new GridLayoutManager(view.getContext(),3,LinearLayoutManager.HORIZONTAL,false));
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(MusicUploadViewModel.class);

        // Khởi tạo ActivityResultLauncher cho việc chọn file
        initImageFilePickerLauncher();
        initFilePickerLauncher();
        // Xử lý sự kiện nhấn nút (Đã cập nhật logic 2 bước: Chọn hoặc Tải lên)
        uploadButton.setOnClickListener(v -> {
                openFilePicker();
        });
        // THÊM: Xử lý sự kiện nhấn nút TẢI LÊN ẢNH
        btn_upanh.setOnClickListener(v -> {
            openImageFilePicker();

        });
        btn_upload.setOnClickListener(v -> {
            if (selectedImageUri == null || selectedMusicUri==null) {
                Toast.makeText(((MainActivity)requireActivity()), "Thêm đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            startImageUploadProcess();
            startUploadProcess();
        });

        ngheSi2Adapter = new NgheSi2Adapter(nghesiList);
        rcv_ns.setAdapter(ngheSi2Adapter);
        NghesiViewModel nghesiViewModel = new ViewModelProvider(requireActivity()).get(NghesiViewModel.class);
        nghesiViewModel.getNghesilis2().observe(getViewLifecycleOwner(),nghesis -> {
            ngheSi1Adapter = new NgheSi_1Adapter(nghesis);
            rcv_tkns.setAdapter(ngheSi1Adapter);
            ngheSi1Adapter.setListener(nghesi->{
                int i=0;
                for(Nghesi ns : nghesiList)
                {
                    if(ns.getMaNgheSi() == nghesi.getMaNgheSi())
                        i++;
                }
                if(i==0)
                {
                    nghesiList.add(nghesi);
                    txt_query.setText("");
                    ngheSi2Adapter.notifyDataSetChanged();
                }

            });

        });
        ngheSi2Adapter.setListener(vt ->{
            nghesiList.remove(vt);
            ngheSi2Adapter.notifyDataSetChanged();
        });
        txt_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(searchRunnable!=null)
                    handler.removeCallbacks(searchRunnable);
                String query = txt_query.getText().toString().trim();
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(!query.isEmpty())
                            nghesiViewModel.search(query);
                        else
                            ngheSi1Adapter.updateData(new ArrayList<>());
                    }
                };
                handler.postDelayed(searchRunnable,DEBOUNCE_DELAY_MS);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });
        txt_query.setOnEditorActionListener((v, actionid, event)->{
            if(actionid==EditorInfo.IME_ACTION_SEARCH)
            {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                String query = txt_query.getText().toString().trim();
                if(!query.isEmpty())
                {
                    nghesiViewModel.search(query);
                }
                else
                    ngheSi1Adapter.updateData(new ArrayList<>());
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Quan sát trạng thái
        viewModel.getUploadState().observe(getViewLifecycleOwner(), this::handleUploadState);
        // Cập nhật trạng thái ban đầu
    }
    private void initFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    // Callback này chạy sau khi người dùng chọn file nhạc
                    if (uri != null) {
                        selectedMusicUri = uri;
                        String fileName = getFileNameFromUri(uri);

                        // 2. Hiển thị lên EditText
                        if (fileName != null) {
                            txt_linkBH.setText(fileName);
                        } else {
                            // Dự phòng nếu không lấy được tên
                            txt_linkBH.setText("File nhạc đã chọn");
                        }
                        Toast.makeText(getContext(), "Đã chọn file nhạc", Toast.LENGTH_SHORT).show();
                        // Bạn có thể cập nhật UI trạng thái ở đây nếu muốn
                    } else {
                        selectedMusicUri = null;
                    }
                }
        );
    }
    // =================================================================
    // 2. CHỌN FILE (ActivityResultLauncher)
    // =================================================================

    private void hideKeyboard() {
        // Lấy View đang được focus hiện tại
        View view = requireActivity().getCurrentFocus();

        if (view != null) {

            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    private void openFilePicker() {
        // Mở trình chọn file và chỉ cho phép chọn file âm thanh
        filePickerLauncher.launch("audio/*");
        Toast.makeText(getContext(), "Vui lòng chọn file nhạc MP3/WAV...", Toast.LENGTH_SHORT).show();
    }

    private void startUploadProcess() {
        if (selectedMusicUri != null) {
            String fileName = "song_" + System.currentTimeMillis() + ".mp3";
            viewModel.startUpload(selectedMusicUri, fileName);
        }
    }

    private void initImageFilePickerLauncher() {
        imageFilePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Picasso.get().load(selectedImageUri).into(img_upload);
                    }else {
                        selectedImageUri = null;
                    }
                }
        );
    }

    private void openImageFilePicker() {
        imageFilePickerLauncher.launch("image/*");
        Toast.makeText(getContext(), "Vui lòng chọn file ảnh (JPEG/PNG)...", Toast.LENGTH_SHORT).show();
    }

    private void startImageUploadProcess() {
        if (selectedImageUri != null) {
            // Tên file ảnh nên có tiền tố khác để dễ phân biệt với nhạc
            String fileName = "cover_" + System.currentTimeMillis() + ".jpg";

            // GIẢ ĐỊNH: Chúng ta sử dụng một phương thức mới trong ViewModel để tải ảnh lên,
            // vì trạng thái tải lên ảnh và nhạc thường là khác nhau.
            // Nếu bạn dùng chung ViewModel, bạn cần sửa đổi logic trong ViewModel.
            viewModel.startImageUpload(selectedImageUri, fileName); // Cần thêm phương thức này vào MusicUploadViewModel
            btn_upanh.setEnabled(false);
        }
    }
    // =================================================================
    // 3. XỬ LÝ TRẠNG THÁI TẢI LÊN (OBSERVER)
    // =================================================================

    private void handleUploadState(UploadState state) {
        // Log.d("UploadFragment", "Status: " + state.getStatus() + ", Message: " + state.getMessageOrUrl());

        switch (state.getStatus()) {
            case UploadState.IDLE:
                progressBar.setVisibility(View.GONE);
                uploadButton.setEnabled(true);
                btn_upanh.setEnabled(true); // Đảm bảo nút ảnh cũng được bật

                break;

            case UploadState.PROGRESS:
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(state.getPercentage());
                // Chỉ hiển thị tiến trình trên statusTextView nếu đang tải nhạc
                // Nếu đang tải ảnh, progress bar có thể dùng chung nhưng status nên tách biệt (hoặc bỏ qua)

                uploadButton.setEnabled(false); // Vô hiệu hóa nút tải nhạc
                btn_upanh.setEnabled(false); // Vô hiệu hóa nút tải ảnh
                break;

            case UploadState.SUCCESS:
                // Tách chuỗi kết quả: "fileType:secureUrl"
                String result = state.getMessageOrUrl();
                if (result != null && result.contains(":")) {
                    String[] parts = result.split(":", 2);
                    String fileType = parts[0];
                    String secureUrl = parts[1];

                    if ("audio".equalsIgnoreCase(fileType)) {
                        // XỬ LÝ KẾT QUẢ TẢI NHẠC
                        uploadedMusicUrl = secureUrl;
                        selectedMusicUri = null;
                        Toast.makeText(getContext(), "Tải file nhạc Cloudinary thành công!", Toast.LENGTH_LONG).show();

                    } else if ("image".equalsIgnoreCase(fileType)) {
                        // XỬ LÝ KẾT QUẢ TẢI ẢNH
                        // Lưu URL ảnh để sử dụng sau (ví dụ: gửi lên DB cùng thông tin bài hát)
                        // (Bạn cần thêm một biến private String imageUrl;)
                        uploadedImageUrl = secureUrl;
                        selectedImageUri = null; // Xóa Uri đã chọn
                        Toast.makeText(getContext(), "Tải ảnh bìa Cloudinary thành công!", Toast.LENGTH_LONG).show();

                    }
                    checkIfReadyToInsert();
                }
                break;

            case UploadState.ERROR:
                // Xử lý lỗi: Cho phép người dùng thử lại
                progressBar.setVisibility(View.GONE);
                uploadButton.setEnabled(true);
                btn_upanh.setEnabled(true); // Bật lại nút ảnh

                // Cập nhật thông báo lỗi, giả định lỗi có thể xảy ra cho cả 2 loại file
                String errorMsg = state.getMessageOrUrl();
                if (errorMsg != null && errorMsg.contains("(image)")) {

                    Toast.makeText(getContext(), "Lỗi tải ảnh bìa!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi tải file nhạc!", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }
    private void checkIfReadyToInsert() {
        if (uploadedMusicUrl != null && uploadedImageUrl != null && !nghesiList.isEmpty()) {

            // 1. Tạo đối tượng với URL ĐÃ TẢI LÊN
            UploadMusic uploadMusic = new UploadMusic(nghesiList,
                    uploadedImageUrl, // Dùng URL Cloudinary
                    txt_tenBH.getText().toString().trim(),
                    uploadedMusicUrl // Dùng URL Cloudinary
            );
            // 2. Gọi ViewModel để INSERT vào DB
            MusicViewModel musicViewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
            musicViewModel.insert(uploadMusic);
            // 3. Reset các biến trạng thái
            uploadedMusicUrl = null;
            progressBar.setVisibility(View.GONE);
            uploadedImageUrl = null;
            img_upload.setImageResource(0);
            txt_tenBH.setText("");
            txt_query.setText("");
            txt_linkBH.setText("");
            uploadButton.setEnabled(true);
            btn_upanh.setEnabled(true);
            nghesiList.clear();
            Toast.makeText(getContext(), "Bài hát đã được thêm thành công vào Server!", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    // Tìm cột chứa tên hiển thị
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Fallback: Nếu không lấy được qua ContentResolver, thử lấy qua đường dẫn
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}