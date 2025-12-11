package com.example.spotify.views;

import android.annotation.SuppressLint;

import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.spotify.R;
import com.example.spotify.Service.MusicService;
import com.example.spotify.adapter.ReelAdapter;
import com.example.spotify.models.Music;
import com.example.spotify.views.fragments.HomeFragment;
import com.example.spotify.views.fragments.WellcomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {


    public Fragment frsave;
    public BottomNavigationView bottomNav;
    public int id;
//    @SuppressLint("MissingInflatedId")
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

// Làm status bar và navigation bar trong suốt
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.BLACK);

        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

// 2. Tìm BottomNavigationView
         bottomNav = findViewById(R.id.bottom_navigation);
        int mauCuaToi = android.graphics.Color.parseColor("#00FFFFFF");
        bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(mauCuaToi));
// 3. QUAN TRỌNG: Liên kết chúng lại với nhau
        NavigationUI.setupWithNavController(bottomNav, navController);

        SharedPreferences sharedPreferences = getSharedPreferences("DangNhap",MODE_PRIVATE);
        String ten = sharedPreferences.getString("TenTK",null);
        SharedPreferences spf = getSharedPreferences("DN",MODE_PRIVATE);
        boolean kt = spf.getBoolean("DangDN",false);
        if (ten != null) {
            // NavOptions giúp xóa lịch sử để khi bấm Back không quay lại màn hình Welcome
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.wellcomeFragment, true)
                    .build();

            // Lưu ý: R.id.homeFragment phải là ID của fragment Home trong nav_graph.xml
            navController.navigate(R.id.nav_home, null, navOptions);
        }
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.wellcomeFragment
                    || destination.getId() == R.id.dangnhapFragment || destination.getId() == R.id.dangnhapemail) { // Thêm ID màn hình đăng nhập của bạn vào đây
                bottomNav.setVisibility(View.GONE);
            } else {
                bottomNav.setVisibility(View.VISIBLE);
            }
        });
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
//    public void addFooter(Fragment fr)
//    {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.contaier_footer,fr,"footer");
//        fragmentTransaction.commit();
//    }

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