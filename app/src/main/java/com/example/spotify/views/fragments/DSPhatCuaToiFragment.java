package com.example.spotify.views.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spotify.R;
import com.example.spotify.adapter.DSphatAdapter;
import com.example.spotify.viewModels.DSPhatViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class DSPhatCuaToiFragment extends BottomSheetDialogFragment {
    private RecyclerView rcv_dsphat;
    private DSphatAdapter dSphatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_d_s_phat_cua_toi, container, false);
        addView(v);

        return v;
    }

    private void addView(View v) {
        SharedPreferences preferences = requireContext().getSharedPreferences("DangNhap",requireContext().MODE_PRIVATE);
        String email = preferences.getString("Email",null);
        String sdt = preferences.getString("SDT",null);
        rcv_dsphat = v.findViewById(R.id.rcv_dsphat);
        rcv_dsphat.setLayoutManager(new LinearLayoutManager(v.getContext()));
        DSPhatViewModel dsPhatViewModel = new ViewModelProvider(requireActivity()).get(DSPhatViewModel.class);
        dsPhatViewModel.getDSPhats().observe(getViewLifecycleOwner(),dsPhats -> {
            dSphatAdapter = new DSphatAdapter(dsPhats);
            rcv_dsphat.setAdapter(dSphatAdapter);
        });
        if(email !=null)
            dsPhatViewModel.dsPhat(email);
        else
            if (sdt!=null)
                dsPhatViewModel.dsPhat(sdt);
    }
}