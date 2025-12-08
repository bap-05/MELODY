package com.example.spotify.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spotify.R;
import com.example.spotify.models.Account;
import com.example.spotify.viewModels.AccountViewModel;


public class HomeFragment extends Fragment {

    public int selectedId =-1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_home, container, false);

        addBody(new HomeTatcaFragment());
        addfragnent(new HeaderHomeFragment());
        selectedId = R.id.btn_tatca;
        SharedPreferences preferences = requireContext().getSharedPreferences("DangNhap", Context.MODE_PRIVATE);
        String ten = preferences.getString("TenTK",null);
        String email = preferences.getString("Email",null);
        String anh = preferences.getString("Anh",null);
        Account account = new Account(ten,anh,email);
        AccountViewModel.setAcc(account);
        return v;
    }
    private void addfragnent (Fragment fr){
        FragmentTransaction ftr = getChildFragmentManager().beginTransaction();
        ftr.add(R.id.contaier_header,fr,"header");
        ftr.commit();
    }
    public void addBody(Fragment fr){
        FragmentTransaction ftr = getChildFragmentManager().beginTransaction();
        ftr.replace(R.id.contaier_body_home,fr);
        ftr.addToBackStack(null);
        ftr.commit();
    }


    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Kiểm tra nếu childFragmentManager có fragment trong back stack
                        HeaderHomeFragment header = (HeaderHomeFragment) getChildFragmentManager().findFragmentByTag("header");
                        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
                            getChildFragmentManager().addOnBackStackChangedListener(() -> {



                                    Fragment current = getChildFragmentManager().findFragmentById(R.id.contaier_body_home);
                                    Log.d("frag",""+current);
                                    if(current instanceof HomeTatcaFragment)
                                    {
                                        header.btn_tatca.setBackgroundResource(R.drawable.custom_btndk);
                                        header.btn_nhac.setBackgroundResource(R.drawable.custom_btn);
                                        header.btn_podcast.setBackgroundResource(R.drawable.custom_btn);
                                        header.btn_podcast.setTextColor(Color.WHITE);
                                        header.btn_nhac.setTextColor(Color.WHITE);
                                        header.btn_tatca.setTextColor(Color.BLACK);
                                        selectedId= R.id.btn_tatca;
                                    }else if(current instanceof HomeNhacFragment)
                                    {
                                        header.btn_nhac.setBackgroundResource(R.drawable.custom_btndk);
                                        header.btn_tatca.setBackgroundResource(R.drawable.custom_btn);
                                        header.btn_podcast.setBackgroundResource(R.drawable.custom_btn);
                                        header.btn_podcast.setTextColor(Color.WHITE);
                                        header.btn_tatca.setTextColor(Color.WHITE);
                                        header.btn_nhac.setTextColor(Color.BLACK);
                                        selectedId = R.id.btn_nhac;
                                    } else if (current instanceof HomePodcastFragment) {
                                        header.btn_podcast.setBackgroundResource(R.drawable.custom_btndk);
                                        header.btn_tatca.setBackgroundResource(R.drawable.custom_btn);
                                        header.btn_nhac.setBackgroundResource(R.drawable.custom_btn);
                                        header.btn_nhac.setTextColor(Color.WHITE);
                                        header.btn_tatca.setTextColor(Color.WHITE);
                                        header.btn_podcast.setTextColor(Color.BLACK);
                                        selectedId= R.id.btn_bodcast;
                                    }

                            });
                            getChildFragmentManager().popBackStack();
                            if(header!=null) {
                                FragmentTransaction ftr = getChildFragmentManager().beginTransaction();
                                ftr.show(header);
                                ftr.commit();
                            }

                        } else {
                            // Nếu không còn, cho phép Activity xử lý bình thường
                            setEnabled(false);
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                }
        );
    }


}


//        layoutAdapter adapter = new layoutAdapter();
//
//        LinearLayoutManager lm = new LinearLayoutManager(v.getContext(),
//                LinearLayoutManager.VERTICAL,
//                false);
//        rcvmusic.setLayoutManager(lm);
//        rcvmusic.setAdapter(adapter);

//        setView(0,v,rcvmusic);
//        setView(1,v,rcvradio);
//        setView(3,v,rcvplaylist);


//    public void setView(int gtri, View v,RecyclerView rcv) {
//        LinearLayoutManager lm = new LinearLayoutManager(v.getContext());
//        if (gtri == 0) {
//            rcv = v.findViewById(R.id.rcvms);
//            musicViewModel viewModel = new musicViewModel();
//
//            List<music> ls = viewModel.setView();   // gọi data
//            musicAdapter msAdapter = new musicAdapter(ls);
//
//            rcv.setLayoutManager(lm);
//            rcv.setAdapter(msAdapter);
//
//        } else if (gtri == 1) {
//            rcv = v.findViewById(R.id.rcv_radio);
//            radioViewModel viewModel = new radioViewModel();
//
//            List<radio> ls = viewModel.setView();
//            radioAdapter radioAdapter = new radioAdapter(ls);
//
//            rcv.setLayoutManager(lm);
//            rcv.setAdapter(radioAdapter);
//
//        } else {
//            rcv = v.findViewById(R.id.rcv_dexuat);
//            playlistViewModel viewModel = new playlistViewModel();
//
//            List<playlist> ls = viewModel.setView();
//            playlistAdapter playlistAdapter = new playlistAdapter(ls);
//
//            rcv.setLayoutManager(lm);
//            rcv.setAdapter(playlistAdapter);
//        }
//    }

