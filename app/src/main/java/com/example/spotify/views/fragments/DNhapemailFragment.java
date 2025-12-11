package com.example.spotify.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.spotify.R;
import com.example.spotify.Repository.AccountRepository;
import com.example.spotify.models.Account;
import com.example.spotify.viewModels.AccountViewModel;
import com.example.spotify.views.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class DNhapemailFragment extends Fragment {
    private Button btn_dnsdt, btn_email;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_d_nhapemail, container, false);
       addView(v);
        mAuth = FirebaseAuth.getInstance();
        // 1️⃣ Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // để lấy ID Token
                .requestEmail()
                .build();
        // 2️⃣ Tạo GoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        // 3️⃣ Đăng ký launcher để nhận kết quả đăng nhập
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            Log.w("GoogleSignIn", "Đăng nhập thất bại", e);
                        }
                    }
                });
        btn_email.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            launcher.launch(signInIntent);
        });
        btn_dnsdt.setOnClickListener(view->{
            ((MainActivity)requireActivity()).openFragment(new DangNhapFragment(),0);
        });
        return v;
    }

    private void addView(View v) {
        btn_dnsdt = v.findViewById(R.id.btn_dn_sdt);
        btn_email = v.findViewById(R.id.btn_dn_email);
    }
    // 5️⃣ Xác thực với Firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        ((MainActivity)requireActivity()).frsave = new HomeFragment();
                        ((MainActivity)requireActivity()).openFragment(((MainActivity)requireActivity()).frsave = new HomeFragment(),0);
                        ((MainActivity)requireActivity()).bottomNav.setVisibility(View.VISIBLE);
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DangNhap", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("TenTK",user.getDisplayName());
                        editor.putString("Email",user.getEmail());
                        editor.putString("Anh",user.getPhotoUrl().toString());
                        editor.apply();
                        Account account = new Account(user.getDisplayName(),user.getPhotoUrl().toString(),user.getEmail());
                        AccountViewModel accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
                        accountViewModel.setAcc(account);
                        accountViewModel.save();
                        Log.d("FirebaseAuth", "Đăng nhập thành công: " + user.getEmail());
                    } else {
                        Log.w("FirebaseAuth", "Đăng nhập thất bại", task.getException());
                    }
                });
    }
}