package com.example.spotify.views.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify.R;
import com.example.spotify.Service.MusicServiceHelper;
import com.example.spotify.adapter.MusicAdapter;
import com.example.spotify.models.Music;
import com.example.spotify.viewModels.NghesiViewModel;
import com.example.spotify.views.MainActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ChiTietNgheSiFragment extends Fragment implements View.OnClickListener{
    private MaterialToolbar toolbar;
    private RecyclerView rcv_phobien, rcv_banmoi;
    private MusicAdapter nhacPhoBienAdapter;
    private Button btn_xemthem;
    private List<Music> ds = new ArrayList<>();
    private List<Music> showList = new ArrayList<>();
    private TextView txt_nghesi;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView img_anhbia;
    private boolean daMoRong = false;
    private String ktra="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_chi_tiet_nghe_si, container, false);
        toolbar =v.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        addView(v);
        addnhacphobien(v);
        btn_xemthem.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
//                HomeFragment homeFragment = (HomeFragment) getParentFragment();
//                if (homeFragment != null) {
//                    HeaderHomeFragment header = (HeaderHomeFragment)
//                            homeFragment.getChildFragmentManager().findFragmentByTag("header");
//
//                    if (header != null) {
//                        FragmentTransaction fr = homeFragment.getChildFragmentManager().beginTransaction();
//                        fr.show(header);
//                        fr.commit();
//                    }
//                }
            }
        });
        return v;
    }

    private void addnhacphobien(View v) {
        rcv_phobien.setLayoutManager(new LinearLayoutManager(v.getContext()));
        NghesiViewModel nghesiViewModel = new ViewModelProvider(requireActivity()).get(NghesiViewModel.class);
        nghesiViewModel.getNghesi().observe(getViewLifecycleOwner(),nghesi -> {
            txt_nghesi.setText(nghesi.getTenNgheSi());
            toolbarLayout.setTitle(nghesi.getTenNgheSi());
            Picasso.get().load(nghesi.getAVT()).into(img_anhbia);
        });
        nghesiViewModel.getLms().observe(getViewLifecycleOwner(),music -> {
            ds.clear();
            showList.clear();
            ds = music;
            for (int i = 0; i < Math.min(3, ds.size()); i++) {
                showList.add(ds.get(i));
            }
            nhacPhoBienAdapter = new MusicAdapter(showList);
            rcv_phobien.setAdapter(nhacPhoBienAdapter);
            nhacPhoBienAdapter.setOnItemClickListener(ms -> {
                for(Music music1 : music)
                    if(music1.getTenBaiHat().equals(ms.getTenBaiHat()))
                    {
                        MusicServiceHelper.setCurrentSong(ms);
                        boolean sameSong = ktra != null && ktra.equals(ms.getTenBaiHat());
                        ((MainActivity) requireActivity()).showFragment(new PlayMusicFragment(), "PlayMusic", sameSong);
                        ((MainActivity) requireActivity()).addFragmentMusic(new ViewMusicFragment(), "music", sameSong);
                        ((MainActivity)requireActivity()).hidenFooter(true);
                        ViewMusicFragment vm1 = (ViewMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("music");
                        PlayMusicFragment pl = (PlayMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("PlayMusic");
                        if (vm1 != null) {
                            vm1.img_stop.setImageResource(R.drawable.stop); // set ngay khi click play
                        }
                        if (pl != null)
                            pl.btn_pause.setImageResource(R.drawable.pause);

                        ((MainActivity)requireActivity()).phatnhac(ms);
                        ktra = ms.getTenBaiHat();
                        break;
                    }
            });
        });
        nghesiViewModel.loadMS();
    }

    private void addView(View v) {
        rcv_banmoi = v.findViewById(R.id.rcv_ctns_banphathanh);
        rcv_phobien = v.findViewById(R.id.rcv_ctns_phobien);
        btn_xemthem = v.findViewById(R.id.btn_ctns_xemthem);
        img_anhbia = v.findViewById(R.id.ctns_headerImage);
        toolbarLayout = v.findViewById(R.id.ctns_collapsingToolbar);
        txt_nghesi = v.findViewById(R.id.ctns_artistName);
        toolbar = v.findViewById(R.id.toolbar);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_ctns_xemthem)
        {
            daMoRong = !daMoRong;
            showList.clear();

            if (daMoRong) {
                showList.addAll(ds);
                btn_xemthem.setText("Thu gọn");
            } else {
                // Hiện tối đa 3 bài
                for (int i = 0; i < Math.min(3, ds.size()); i++) {
                    showList.add(ds.get(i));
                }
                btn_xemthem.setText("Xem thêm");
            }
            nhacPhoBienAdapter = new MusicAdapter(showList);
            rcv_phobien.setAdapter(nhacPhoBienAdapter);
        }
    }
}