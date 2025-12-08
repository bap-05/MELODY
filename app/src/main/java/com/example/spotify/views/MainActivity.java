package com.example.spotify.views;

import android.annotation.SuppressLint;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cloudinary.android.MediaManager;
import com.example.spotify.R;
import com.example.spotify.Service.MusicService;
import com.example.spotify.Service.MusicServiceHelper;
import com.example.spotify.adapter.ReelAdapter;
import com.example.spotify.models.Music;
import com.example.spotify.views.fragments.DNhapemailFragment;
import com.example.spotify.views.fragments.FooterFragment;
import com.example.spotify.views.fragments.HomeFragment;
import com.example.spotify.views.fragments.PlayMusicFragment;
import com.example.spotify.views.fragments.ReelFragment;
import com.example.spotify.views.fragments.UploadMusicFragment;
import com.example.spotify.views.fragments.WellcomeFragment;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    public Fragment frsave;
//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

// Làm status bar và navigation bar trong suốt
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.BLACK);

        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences = getSharedPreferences("DangNhap",MODE_PRIVATE);
        String ten = sharedPreferences.getString("TenTK",null);
        SharedPreferences spf = getSharedPreferences("DN",MODE_PRIVATE);
        boolean kt = spf.getBoolean("DangDN",false);
        if(ten!=null)
        {
            frsave = new HomeFragment();
            replaceFragment(frsave,false,0);
            addFooter(new FooterFragment());
        }

        else
        {
            frsave = new WellcomeFragment();
            replaceFragment(frsave,false ,0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

    }

    private void replaceFragment(Fragment fr, boolean addToBackStack ,int kt ) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(kt ==0) {

            transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
            );
        }
        else
        {
            transaction.setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
            );
        }
        transaction.replace(R.id.container_body,fr);
        if(fr.getClass().equals(frsave.getClass())){
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            addToBackStack = false;
        }
        if(addToBackStack)
            transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
    public void openFragment(Fragment fr, int kt){
        replaceFragment(fr,true, kt);
    }

   public void showFragment(Fragment fragment, String tag,boolean kt ) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment existing = fm.findFragmentByTag(tag);
        if (existing == null) {
            ft.add(R.id.container_music, fragment, tag);
        } else if(kt == false)
        {
            ft.remove(existing);
            ft.add(R.id.container_music, fragment, tag);
        }
        else
        {
            ft.show(existing);
        }

        ft.commit();
    }
    public void addFragmentMusic(Fragment fragment, String tag, boolean kt) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment existing = fm.findFragmentByTag(tag);
        if (existing != null && !kt) {
            ft.remove(existing);
        }
        if(existing == null || !kt)
        {
            ft.add(R.id.view_music, fragment, tag);
            ft.hide(fragment);


        }else
        {
            ft.hide(existing);
        }
        ft.commit();
    }
    public void addFooter(Fragment fr)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contaier_footer,fr,"footer");
        fragmentTransaction.commit();
    }

    public void phatnhac(Music ms){

        Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
        serviceIntent.setAction("PLAY");
        serviceIntent.putExtra("music",ms);
        startService(serviceIntent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Dừng video hoặc animation ở đây
        if (ReelAdapter.exoPlayer != null) {
            ReelAdapter.exoPlayer.pause();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ReelAdapter.exoPlayer != null) {
            ReelAdapter.exoPlayer.pause();
            ReelAdapter.exoPlayer.release();
            ReelAdapter.exoPlayer = null; // Gán lại null để chắc chắn
        }

    }
    public void hidenFooter(boolean ktra){
        Fragment footer = getSupportFragmentManager().findFragmentByTag("footer");
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        if(footer!=null)
        {
            if(ktra)
                transaction.hide(footer);
            else
                transaction.show(footer);
            transaction.commit();
        }
    }
}