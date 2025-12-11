package com.example.spotify.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spotify.R;
import com.example.spotify.Service.MusicServiceHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChiTietMusicFragment extends BottomSheetDialogFragment {
    private TextView txt_name, txt_share, txt_themvaods;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chi_tiet_music, container, false);
        addView(v);
        MusicServiceHelper.getMusic().observe(getViewLifecycleOwner(),ms->{
            txt_name.setText(ms.getTenBaiHat());
        });
        txt_themvaods.setOnClickListener(view ->{
            DSPhatCuaToiFragment dsPhatCuaToiFragment = new DSPhatCuaToiFragment();
            dsPhatCuaToiFragment.show(requireActivity().getSupportFragmentManager(),"DanhSachPhat");
        });
        return v;
    }

    private void addView(View v) {
        txt_name = v.findViewById(R.id.txtSongName);
        txt_share= v.findViewById(R.id.btnShare);
        txt_themvaods= v.findViewById(R.id.btnAddToPlaylist);

    }
}