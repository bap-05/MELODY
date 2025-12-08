package com.example.spotify.views.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.spotify.R;
import com.example.spotify.Service.MusicServiceHelper;
import com.example.spotify.adapter.MusicAdapter;
import com.example.spotify.adapter.NgheSi_1Adapter;
import com.example.spotify.viewModels.MusicViewModel;
import com.example.spotify.viewModels.NghesiViewModel;
import com.example.spotify.views.MainActivity;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private ImageButton btn_back;
    private Runnable searchRunnable;
    private Handler handler = new Handler(Looper.getMainLooper());
    private EditText txt_search;
    private MusicAdapter musicAdapter;
    private NgheSi_1Adapter ngheSi1Adapter;
    private RecyclerView rcv_search;
    private ConcatAdapter concatAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        addView(v);
        ngheSi1Adapter = new NgheSi_1Adapter(new ArrayList<>()); // Khởi tạo rỗng
        musicAdapter = new MusicAdapter(new ArrayList<>());
        concatAdapter = new ConcatAdapter(musicAdapter, ngheSi1Adapter);
        rcv_search.setLayoutManager(new LinearLayoutManager(v.getContext()));
        rcv_search.setAdapter(concatAdapter);
        NghesiViewModel nghesiViewModel = new ViewModelProvider(requireActivity()).get(NghesiViewModel.class);
        nghesiViewModel.getNghesilis2().observe(getViewLifecycleOwner(),nslist->{
            if (nslist != null) {
                ngheSi1Adapter.updateData(nslist); // Bạn cần viết hàm updateData trong Adapter
                ngheSi1Adapter.setListener(ns->{
                    nghesiViewModel.setNghesi(ns);
                    FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                    fr.add(R.id.container_body,new ChiTietNgheSiFragment());
                    fr.addToBackStack(null);
                    fr.commit();
                });
            } else {
                ngheSi1Adapter.updateData(new ArrayList<>());
            }
        });
        MusicViewModel musicViewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        musicViewModel.getListMusic().observe(getViewLifecycleOwner(),listMS->{
            if (listMS != null) {
                musicAdapter.updateData(listMS); // Bạn cần viết hàm updateData trong Adapter
                musicAdapter.setOnItemClickListener(ms->{
                    hideKeyboard();
                    MusicServiceHelper.setCurrentSong(ms);
                    ViewMusicFragment vm = new ViewMusicFragment();
                    PlayMusicFragment pl = new PlayMusicFragment();
                    ((MainActivity)requireActivity()).hidenFooter(true);
                    ((MainActivity) requireActivity()).showFragment(pl, "PlayMusic", false);
                    ((MainActivity) requireActivity()).addFragmentMusic(vm, "music", false);
                    ViewMusicFragment vm1 = (ViewMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("music");
                    PlayMusicFragment pl1 = (PlayMusicFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("PlayMusic");
                    if (vm1 != null) {
                        vm1.img_stop.setImageResource(R.drawable.stop); // set ngay khi click play
                    }
                    if (pl1 != null)
                        pl1.btn_pause.setImageResource(R.drawable.pause);
                    ((MainActivity)requireActivity()).phatnhac(ms);
                });
            } else {
                musicAdapter.updateData(new ArrayList<>());
            }

        });

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(searchRunnable !=null)
                    handler.removeCallbacks(searchRunnable);
                String query = txt_search.getText().toString().trim();
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(!query.isEmpty())
                        {
                            musicViewModel.search(query);
                            nghesiViewModel.search(query);
                        }
                        else
                        {
                            ngheSi1Adapter.updateData(new ArrayList<>());
                            musicAdapter.updateData(new ArrayList<>());
                        }
                    }
                };
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });
        txt_search.setOnEditorActionListener((v1, actionId, event) -> {
            if(actionId== EditorInfo.IME_ACTION_SEARCH)
            {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                String query = txt_search.getText().toString().trim();
                if(!query.isEmpty())
                {
                    musicViewModel.search(query);
                    nghesiViewModel.search(query);
                }
                else
                {
                    ngheSi1Adapter.updateData(new ArrayList<>());
                    musicAdapter.updateData(new ArrayList<>());
                }
                hideKeyboard();
                return true;
            }

            return false;
        });

        return v;
    }

    private void addView(View v) {
        btn_back = v.findViewById(R.id.btn_search_back);
        txt_search = v.findViewById(R.id.txt_search_tk);
        rcv_search = v.findViewById(R.id.rcv_search);
    }
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
}