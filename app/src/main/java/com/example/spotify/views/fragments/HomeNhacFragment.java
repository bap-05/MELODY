package com.example.spotify.views.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.spotify.R;
import com.example.spotify.adapter.NghesiAdapter;
import com.example.spotify.adapter.PlaylistAdapter;
import com.example.spotify.adapter.RadioAdapter;
import com.example.spotify.models.Nghesi;
import com.example.spotify.models.Playlist;
import com.example.spotify.models.Radio;
import com.example.spotify.viewModels.NghesiViewModel;
import com.example.spotify.viewModels.PlaylistViewModel;
import com.example.spotify.viewModels.RadioViewModel;
import com.example.spotify.views.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class HomeNhacFragment extends Fragment {
    private RecyclerView rdo,rcv_nhesi, rcv_album, rcvradio;
    private List<Radio> Lrdo;
    private RadioAdapter radioAdapter;
    private List<Nghesi> lNgheSi;
    private NghesiAdapter nghesiAdapter;
    private PlaylistAdapter albumAdapter;
    private List<Playlist> lAlbum;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View v =inflater.inflate(R.layout.fragment_home_nhac, container, false);
        addViewRadio(v);
      addViewNS(v);
      addViewAlbum(v);

      return v;
    }

    private void addViewAlbum(View v) {
        rcv_album = v.findViewById(R.id.rcv_nhac_album);
        lAlbum = new ArrayList<>();
        PlaylistViewModel albumVM = new PlaylistViewModel();
        lAlbum = albumVM.setView();
        albumAdapter = new PlaylistAdapter(lAlbum);
        LinearLayoutManager lm = new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL,false);
        rcv_album.setLayoutManager(lm);
        rcv_album.setAdapter(albumAdapter);
    }

    private void addViewNS(View v) {
        rcv_nhesi = v.findViewById(R.id.rcv_nhac_nghesi);
        rcv_nhesi.setLayoutManager(new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL,false));
        NghesiViewModel nghesiViewModel = new ViewModelProvider(requireActivity()).get(NghesiViewModel.class);
        nghesiViewModel.getNghesilist().observe(getViewLifecycleOwner(),ns->{
            nghesiAdapter = new NghesiAdapter(ns);
            rcv_nhesi.setAdapter(nghesiAdapter);
            nghesiAdapter.setListener(ns1 -> {
                for(Nghesi nghesi : ns)
                {
                    if(nghesi.getMaNgheSi()==ns1.getMaNgheSi())
                    {
                        nghesiViewModel.setNghesi(ns1);
//                        HomeFragment homeFragment = (HomeFragment) getParentFragment();
//                        if (homeFragment != null) {
//                            HeaderHomeFragment header = (HeaderHomeFragment)
//                                    homeFragment.getChildFragmentManager().findFragmentByTag("header");
//
//                            if (header != null) {
//                                FragmentTransaction fr = homeFragment.getChildFragmentManager().beginTransaction();
//                                fr.hide(header);
//                                fr.commit();
//                            }
//                        }
//                        ((HomeFragment)getParentFragment()).addBody(new ChiTietNgheSiFragment());

                        Navigation.findNavController(v).navigate(R.id.chitietns);
                    }

                }
            });
        });
        nghesiViewModel.loadNS();


    }

    private void addViewRadio(View v) {
        rcvradio=v.findViewById(R.id.rcv_radio2);

        rcvradio.setLayoutManager(new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL,false));
        RadioViewModel rdoVM = new ViewModelProvider(requireActivity()).get(RadioViewModel.class);
        rdoVM.getRadioList().observe(getViewLifecycleOwner(),radios -> {
            if(radios!=null)
            {
                radioAdapter = new RadioAdapter(radios);
                rcvradio.setAdapter(radioAdapter);
                radioAdapter.setListener(radio->{
                    for(Radio rd : radios)
                    {
                        if(rd.getMaRadio() == radio.getMaRadio())
                        {
                            RadioViewModel.setRd(radio);
                            Navigation.findNavController(v).navigate(R.id.chitiet_radio);
                        }
                    }
                });
            }
        });
        rdoVM.getErrorMessage().observe(getViewLifecycleOwner(),err ->{
            if(err != null)
                Toast.makeText(v.getContext(),"Lỗi tải dữ liệu: "+err,Toast.LENGTH_LONG).show();
        });
        rdoVM.loadRadio();
    }
}