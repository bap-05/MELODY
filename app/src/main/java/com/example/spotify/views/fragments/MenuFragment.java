package com.example.spotify.views.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spotify.R;
import com.example.spotify.viewModels.AccountViewModel;
import com.example.spotify.views.MainActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

public class MenuFragment extends Fragment implements View.OnClickListener {
    private ShapeableImageView img_profile;
    private ImageView img_close;
    private TextView txt_profile1, txt_profile2;
    private LinearLayout lo_themns, lo_banmoi,lo_ganday,lo_dangxuat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_menu, container, false);
        addView(v);
        img_close.setOnClickListener(this);
        lo_dangxuat.setOnClickListener(this);
        AccountViewModel.getAcc().observe(getViewLifecycleOwner(), account -> {
            Picasso.get().load(account.getAVT()).into(img_profile);
            txt_profile1.setText(account.getTenTK());
        });
//        lo_themns.setOnClickListener(this);
//        lo_banmoi.setOnClickListener(this);
        return v;
    }

    private void addView(View v) {
        img_profile = v.findViewById(R.id.img_profile);
        img_close = v.findViewById(R.id.img_close);
        txt_profile1 = v.findViewById(R.id.txt_profile1);
        txt_profile2 = v.findViewById(R.id.txt_profile2);
//        lo_themns = v.findViewById(R.id.menu_them_ns);
//        lo_banmoi = v.findViewById(R.id.menu_uploadnhac);
        lo_ganday = v.findViewById(R.id.gan_day);
        lo_dangxuat = v.findViewById(R.id.dang_xuat);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.img_close)
        {

            requireActivity().getSupportFragmentManager().popBackStack();


        }
        if(v.getId()==R.id.dang_xuat)
        {
            ((MainActivity)requireActivity()).frsave = new WellcomeFragment();
            ((MainActivity)requireActivity()).openFragment(((MainActivity)requireActivity()).frsave,0);
            SharedPreferences sp = requireContext().getSharedPreferences("DN",MODE_PRIVATE);
            SharedPreferences.Editor edt = sp.edit();
            edt.putBoolean("DangDN",false);
            edt.apply();
            PlayMusicFragment playlist = (PlayMusicFragment) getParentFragmentManager().findFragmentByTag("PlayMusic");
            ViewMusicFragment viewMuic = (ViewMusicFragment) getParentFragmentManager().findFragmentByTag("music");
            if (playlist != null) {
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.remove(playlist);
                fr.commit();
            }
            if (viewMuic != null) {
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.remove(viewMuic);
                fr.commit();
            }
        }
//        if(v.getId()==R.id.menu_them_ns)
//        {
//            FooterFragment.id = 0;
//            ViewMusicFragment music = (ViewMusicFragment) getParentFragmentManager().findFragmentByTag("music");
//            if(music!=null)
//            {
//                FragmentTransaction fr = (requireActivity()).getSupportFragmentManager().beginTransaction();
//                fr.hide(music);
//                fr.commit();
//            }
//            ((MainActivity)requireActivity()).openFragment(new ThemNSFragment(),0);
//        }
//        if(v.getId()==R.id.menu_uploadnhac)
//        {
//            FooterFragment.id = 0;
//            ViewMusicFragment music = (ViewMusicFragment) getParentFragmentManager().findFragmentByTag("music");
//            if(music!=null)
//            {
//                FragmentTransaction fr = (requireActivity()).getSupportFragmentManager().beginTransaction();
//                fr.hide(music);
//                fr.commit();
//            }
//            ((MainActivity)requireActivity()).openFragment(new UploadMusicFragment(),0);
//        }

    }
}