package com.example.spotify.views.fragments;

import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.spotify.R;
import com.example.spotify.Service.MusicService;
import com.example.spotify.Service.MusicServiceHelper;
import com.example.spotify.views.MainActivity;

public class FooterFragment extends Fragment implements View.OnClickListener {
    private ImageButton btn_home, btn_pre, btn_reel,btn_search;
    public static int id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_footer, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(v, (v1, windowInsets) -> {
            // Lấy thông số của các thanh hệ thống (Status bar, Nav bar)
            Insets insets = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();
            }

            // Cộng thêm padding bên dưới cho Footer bằng đúng chiều cao thanh điều hướng
            // (Giữ nguyên padding trái/phải/trên cũ, chỉ cộng thêm cái bottom)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), insets.bottom);
            }

            return windowInsets;
        });
        addView(v);
        id = R.id.imgbtn_Home;
        btn_home.setOnClickListener(this);
        btn_reel.setOnClickListener(this);
//        btn_pre.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        return v ;
    }

    private void addView(View v) {
        btn_home = v.findViewById(R.id.imgbtn_Home);
        btn_pre = v.findViewById(R.id.imgbtn_Pre);
        btn_reel = v.findViewById(R.id.imgbtn_Lib);
        btn_search = v.findViewById(R.id.imgbtn_Search);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imgbtn_Home && id != R.id.imgbtn_Home)
        {

            ((MainActivity)requireActivity()).frsave=new HomeFragment();
            ((MainActivity)requireActivity()).openFragment(((MainActivity)requireActivity()).frsave=new HomeFragment(),1);

            ViewMusicFragment music = (ViewMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("music");
            if(music!=null)
            {
                FragmentTransaction fr = (requireActivity()).getSupportFragmentManager().beginTransaction();
                fr.show(music);
                fr.commit();
            }
            btn_home.setImageResource(R.drawable.home);
            btn_reel.setImageResource(R.drawable.library);
            btn_search.setImageResource(R.drawable.search);
            id = R.id.imgbtn_Home;
        }
        if(v.getId() == R.id.imgbtn_Lib && id != R.id.imgbtn_Lib)
        {
            int kt;
//            if(MusicServiceHelper.isPlaying())
//            {
//                Intent intent = new Intent(requireContext(), MusicService.class);
//                intent.setAction("PAUSE");
//                requireContext().startService(intent);
//            }
            ViewMusicFragment music = (ViewMusicFragment) getParentFragmentManager().findFragmentByTag("music");
            if(music!=null)
            {
                FragmentTransaction fr = (requireActivity()).getSupportFragmentManager().beginTransaction();
                fr.hide(music);
                fr.commit();
            }

            if(id==R.id.imgbtn_Home ||id==R.id.imgbtn_Search)
                kt=0;
            else
                kt=1;
//            if(id==R.id.imgbtn_Pre)
//                kt=1;
            ((MainActivity)requireActivity()).openFragment(new ReelFragment(),kt);
            btn_reel.setImageResource(R.drawable.library1);
            btn_home.setImageResource(R.drawable.home1);
            btn_search.setImageResource(R.drawable.search);
            id = R.id.imgbtn_Lib;

        }
        if(v.getId() == R.id.imgbtn_Search && id != R.id.imgbtn_Search)
        {
            int kt;
            if(id==R.id.imgbtn_Home )
                kt=0;
            else
                kt=1;
//            if(id==R.id.imgbtn_Pre)
//                kt=1;
            ((MainActivity)requireActivity()).openFragment(new SearchFragment(),kt);
            btn_reel.setImageResource(R.drawable.library);
            btn_home.setImageResource(R.drawable.home1);
            btn_search.setImageResource(R.drawable.search1);
            id = R.id.imgbtn_Search;

        }

    }
}