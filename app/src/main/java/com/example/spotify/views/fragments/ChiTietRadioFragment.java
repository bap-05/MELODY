package com.example.spotify.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotify.R;
import com.example.spotify.Service.MusicService;
import com.example.spotify.Service.MusicServiceHelper;
import com.example.spotify.adapter.MusicAdapter;
import com.example.spotify.adapter.RadioAdapter;
import com.example.spotify.models.Music;
import com.example.spotify.models.Radio;
import com.example.spotify.viewModels.MusicViewModel;
import com.example.spotify.viewModels.RadioViewModel;
import com.example.spotify.views.MainActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChiTietRadioFragment extends Fragment implements View.OnClickListener {
    private ImageButton btn_ql, btn_phat;
    private ShapeableImageView img_cd1,img_cd2,img_cd3;
    private TextView txt_ten, txt_ns;
    private RecyclerView rcv_ct;

    private MusicAdapter musicAdapter;
    private List<Music> musicList;
    private String ten;
    private String ktra="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chi_tiet_radio, container, false);
        addRadio(v);
        addView(v);
        themnhac(v);
        Log.d("ten222",""+ten);
        btn_phat.setOnClickListener(this);
        btn_ql.setOnClickListener(this);
        return v;
    }

    private void addRadio(View v) {
        img_cd1 = v.findViewById(R.id.ct_CD1);
        img_cd2 = v.findViewById(R.id.ct_CD2);
        img_cd3 = v.findViewById(R.id.ct_CD3);
        txt_ten = v.findViewById(R.id.txt_ct_name);
        txt_ns = v.findViewById(R.id.txt_nsthamgia);
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Music ms) {
                MusicServiceHelper.setCurrentSong(ms);
                ViewMusicFragment vm = new ViewMusicFragment();
                PlayMusicFragment pl = new PlayMusicFragment();
//                            vm.setArguments(bl);
//                            pl.setArguments(bl);

                boolean sameSong = ktra != null && ktra.equals(ms.getTenBaiHat());
                ((MainActivity)requireActivity()).hidenFooter(true);
                ((MainActivity) requireActivity()).showFragment(pl, "PlayMusic", sameSong);
                ((MainActivity) requireActivity()).addFragmentMusic(vm, "music", sameSong);
                ((MainActivity) requireActivity()).bottomNav.setVisibility(v.GONE);
                ViewMusicFragment vm1 = (ViewMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("music");
                PlayMusicFragment pl1 = (PlayMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("PlayMusic");
                if (vm1 != null) {
                    vm1.img_stop.setImageResource(R.drawable.stop); // set ngay khi click play
                }
                if (pl1 != null)
                    pl1.btn_pause.setImageResource(R.drawable.pause);
                ((MainActivity)requireActivity()).phatnhac(ms);
                ktra = ms.getTenBaiHat();
            }

            @Override
            public void onMoreClick(Music ms) {
                MusicServiceHelper.setMusic(ms);
                ChiTietMusicFragment chiTietMusicFragment = new ChiTietMusicFragment();
                chiTietMusicFragment.show(requireActivity().getSupportFragmentManager(),"more");
            }
        });
        RadioViewModel.getRd().observe(getViewLifecycleOwner(),radio -> {
            Picasso.get().load(radio.getAnh1()).into(img_cd3);
            Picasso.get().load(radio.getAnh2()).into(img_cd2);
            Picasso.get().load(radio.getAnh3()).into(img_cd1);
            txt_ten.setText(radio.getTenRadio());
            txt_ns.setText("Với "+radio.getTenNgheSi());
            ten = radio.getTenNgheSi();
            rcv_ct.setLayoutManager( new LinearLayoutManager(v.getContext()));
            MusicViewModel music = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);

            music.loadMusicRadio(radio);
        });
    }


    private void addView(View v) {
        btn_ql = v.findViewById(R.id.btn_ct_ql);
        btn_phat = v.findViewById(R.id.btn_ct_phat);
        rcv_ct = v.findViewById(R.id.rcv_ct_radio);


    }
    private void themnhac(View v) {
        MusicViewModel mvd = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        mvd.getMusicList3().observe(getViewLifecycleOwner(), msl ->{
            ArrayList<Music> playlist = new ArrayList<>(msl);
            Intent intent = new Intent(requireContext(), MusicService.class);
            intent.setAction("PLAYLIST");
            intent.putExtra("playlist",playlist);
            requireContext().startService(intent);
        });
        mvd.getallmusic();
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_ct_phat)
        {
            MusicViewModel mvd = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
            mvd.getMusicList3().observe(getViewLifecycleOwner(),msl->{
                MusicServiceHelper.setCurrentSong(msl.get(0));
                boolean sameSong = ktra != null && ktra.equals(msl.get(0).getTenBaiHat());
                ((MainActivity)requireActivity()).hidenFooter(true);
                ((MainActivity) requireActivity()).showFragment(new PlayMusicFragment(), "PlayMusic", sameSong);
                ((MainActivity) requireActivity()).addFragmentMusic(new ViewMusicFragment(), "music", sameSong);
                ViewMusicFragment vm1 = (ViewMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("music");
                PlayMusicFragment pl = (PlayMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("PlayMusic");
                if (vm1 != null) {
                    vm1.img_stop.setImageResource(R.drawable.stop); // set ngay khi click play
                }
                if (pl != null)
                    pl.btn_pause.setImageResource(R.drawable.pause);
                ((MainActivity)requireActivity()).phatnhac(msl.get(0));
                ktra = msl.get(0).getTenBaiHat();

            });

        }
        if(v.getId()==R.id.btn_ct_ql)
        {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
}