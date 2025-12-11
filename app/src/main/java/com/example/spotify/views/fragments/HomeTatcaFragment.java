package com.example.spotify.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.spotify.API.ApiCallback;
import com.example.spotify.API.ApiClient;
import com.example.spotify.API.ApiResponse;
import com.example.spotify.API.IAPI;
import com.example.spotify.R;
import com.example.spotify.Service.MusicService;
import com.example.spotify.Service.MusicServiceHelper;
import com.example.spotify.adapter.MusicAdapter;
import com.example.spotify.adapter.NghesiAdapter;
import com.example.spotify.adapter.PlaylistAdapter;
import com.example.spotify.adapter.PodcastAdapter;
import com.example.spotify.adapter.PodcastTatcaAdapter;
import com.example.spotify.adapter.RadioAdapter;
import com.example.spotify.models.Music;
import com.example.spotify.models.Nghesi;
import com.example.spotify.models.Playlist;
import com.example.spotify.models.Podcast;
import com.example.spotify.models.Radio;
import com.example.spotify.viewModels.MusicViewModel;
import com.example.spotify.viewModels.NghesiViewModel;
import com.example.spotify.viewModels.PlaylistViewModel;
import com.example.spotify.viewModels.PodcastViewModel;
import com.example.spotify.viewModels.RadioViewModel;
import com.example.spotify.views.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class HomeTatcaFragment extends Fragment {
    private RecyclerView rcvmusic,rcvradio,rcvplaylist, rcvnghesi,rcv_podcast;
    private List<Music> mListmusic;
    private MusicAdapter msAdapter;
    private List<Radio> mListradio;
    private RadioAdapter radioAdapter;
    private List<Playlist> lplay;
    private List<Nghesi> lnghesi;
    private NghesiAdapter nsAdapter;
    private PlaylistAdapter playlistAdapter;
    private PodcastTatcaAdapter podcastAdapter;
    private List<Podcast> lPodcast;
    int i =0;

    public String ktra="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_tatca, container, false);

        addView(v);
        themnhac(v);
        SharedPreferences prefs = requireContext().getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
        String ten = prefs.getString("tenBaiHat", null);
        String tacGia = prefs.getString("tacGia", null);
        String anh = prefs.getString("anh", null);
        String url = prefs.getString("url", null);

        if (ten != null) {
            Bundle bl = new Bundle();
            bl.putString("TenBaiHat", ten);
            bl.putString("Anh", anh);
            bl.putString("TacGia", tacGia);
            bl.putString("url", url);
            ViewMusicFragment vm = new ViewMusicFragment();
            PlayMusicFragment pl = new PlayMusicFragment();
            vm.setArguments(bl);
            pl.setArguments(bl);
            FragmentManager fm = ((MainActivity)requireActivity()).getSupportFragmentManager();
            FragmentTransaction fr = fm.beginTransaction();
            fr.add(R.id.view_music, vm, "music");
            fr.add(R.id.container_body, pl, "PlayMusic");
            fr.hide(pl);
            fr.commit();
        }
        addViewNS(v);
        addViewPodcast(v);
        addViewRadio(v);
        return v;
    }

    private void addViewNS(View v) {
        rcvnghesi = v.findViewById(R.id.rcv_nghesi);
        rcvnghesi.setLayoutManager(new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL,false));
        NghesiViewModel nghesiViewModel = new ViewModelProvider(requireActivity()).get(NghesiViewModel.class);
        nghesiViewModel.getNghesilist().observe(getViewLifecycleOwner(),ns->{
            nsAdapter = new NghesiAdapter(ns);
            rcvnghesi.setAdapter(nsAdapter);
            nsAdapter.setListener(ns1 -> {
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
//                        FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
//                        fr.add(R.id.container_body,new ChiTietNgheSiFragment());
//                        fr.addToBackStack(null);
//                        fr.commit();
                        Navigation.findNavController(v).navigate(R.id.chitietns);

                    }

                }
            });
        });
        nghesiViewModel.loadNS();

    }

    private void themnhac(View v) {
        MusicViewModel mvd = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        mvd.getMusicList2().observe(getViewLifecycleOwner(), msl ->{
            ArrayList<Music> playlist = new ArrayList<>(msl);
            Intent intent = new Intent(requireContext(), MusicService.class);
            intent.setAction("PLAYLIST");
            intent.putExtra("playlist",playlist);
            requireContext().startService(intent);
        });
        mvd.getallmusic();
    }

    private void addViewRadio(View v) {
        rcvradio=v.findViewById(R.id.rcv_radio);

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

    private void addViewPodcast(View v) {
        rcv_podcast = v.findViewById(R.id.rcv_tatca_padcast);
        lPodcast = new ArrayList<>();
        PodcastViewModel podcastVM = new PodcastViewModel();
        lPodcast = podcastVM.setPodcast();
        podcastAdapter = new PodcastTatcaAdapter(lPodcast);
        LinearLayoutManager lm = new LinearLayoutManager(v.getContext());
        rcv_podcast.setLayoutManager(lm);
        rcv_podcast.setAdapter(podcastAdapter);
    }


    private void addView(View v) {

        rcvmusic = v.findViewById(R.id.rcvms);
        rcvmusic.setLayoutManager(new LinearLayoutManager(v.getContext()));

        MusicViewModel viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);

        viewModel.getMusicList().observe(getViewLifecycleOwner(), musicList -> {
            if (musicList != null) {
                msAdapter = new MusicAdapter(musicList);
                rcvmusic.setAdapter(msAdapter);
                msAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {

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
                        phatnhac(ms);
                        ktra = ms.getTenBaiHat();
                    }

                    @Override
                    public void onMoreClick(Music ms) {
                        MusicServiceHelper.setMusic(ms);
                        ChiTietMusicFragment chiTietMusicFragment = new ChiTietMusicFragment();
                        chiTietMusicFragment.show(requireActivity().getSupportFragmentManager(),"more");
                    }
                });
            }
        });


        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(v.getContext(), "Lỗi tải nhạc: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadMusic();
        if(viewModel.kt==0)
        {
            SharedPreferences prefs = requireContext().getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

        }

        //view radio

        // view playlist
        rcvplaylist=v.findViewById(R.id.rcv_dexuat);
        lplay= new ArrayList<>();
        PlaylistViewModel plvd = new PlaylistViewModel();
        lplay = plvd.setView();
        playlistAdapter = new PlaylistAdapter(lplay);
        LinearLayoutManager lm2 = new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL,false);
        rcvplaylist.setLayoutManager(lm2);
        rcvplaylist.setAdapter(playlistAdapter);
        // View nghe si
//        rcvnghesi = v.findViewById(R.id.rcv_nghesi);
//        lnghesi = new ArrayList<>();
//        NghesiViewModel nsVM = new NghesiViewModel();
//        lnghesi = nsVM.addView();
//        nsAdapter = new NghesiAdapter(lnghesi);
//        LinearLayoutManager lm3 = new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL, false);
//        rcvnghesi.setLayoutManager(lm3);
//        rcvnghesi.setAdapter(nsAdapter);
    }
    public void phatnhac(Music ms){

        Intent serviceIntent = new Intent(requireContext(), MusicService.class);
        serviceIntent.setAction("PLAY");
//        serviceIntent.putExtra("url", url);
//        serviceIntent.putExtra("tenbai", tenBai);
//        serviceIntent.putExtra("tacgia", tacGia);
//        serviceIntent.putExtra("anh", anh);
        serviceIntent.putExtra("music",ms);
        requireContext().startService(serviceIntent);
    }
}