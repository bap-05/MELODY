package com.example.spotify.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.exoplayer.ExoPlayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spotify.R;
import com.example.spotify.Service.MusicService;
import com.example.spotify.Service.MusicServiceHelper;
import com.example.spotify.viewModels.MusicViewModel;
import com.example.spotify.views.MainActivity;
import com.squareup.picasso.Picasso;


public class ViewMusicFragment extends Fragment implements View.OnClickListener{
    public ImageView img_stop, img_them,img_anh;
    private LinearLayout layout_view_music;
    private TextView txt_baiHat, txt_tacGia;
    private String url,anh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_music, container, false);
        addView(v);
        img_stop.setOnClickListener(this);
        layout_view_music.setOnClickListener(this);
        SharedPreferences prefs = requireContext().getSharedPreferences("music_prefs", Context.MODE_PRIVATE);
        String phatNhac = prefs.getString("phatnhac",null);
        if(phatNhac!=null && phatNhac.equals("no"))
            Picasso.get().load(R.drawable.play_buttton1).into(img_stop);

        MusicViewModel.getIsPreparing().observe(getViewLifecycleOwner(), x->{
            if(x)
            {
               img_stop.setEnabled(false);

            }
            else
            {
                img_stop.setEnabled(true);

            }
        });
        MusicServiceHelper.getPhat().observe(getViewLifecycleOwner(), kt->{
            if(kt)
                img_stop.setImageResource(R.drawable.stop);
            else
                img_stop.setImageResource(R.drawable.play_buttton1);
        });
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MusicServiceHelper.getCurrentSong().observe(getViewLifecycleOwner(), music -> {
            if(music !=null)
            {
                txt_baiHat.setText(music.getTenBaiHat());
                txt_tacGia.setText(music.getTenNgheSi());
                Picasso.get().load(music.getAnh()).into(img_anh);
            }
        });
    }

    private void addView(View v) {
        img_stop = v.findViewById(R.id.vm_stop);
        img_them = v.findViewById(R.id.vm_them);
        img_anh=v.findViewById(R.id.img_view_music_anh);
        txt_baiHat = v.findViewById(R.id.vm_baiHat);
        txt_tacGia = v.findViewById(R.id.vm_tentacgia);
        layout_view_music = v.findViewById(R.id.layout_view_music);
        txt_baiHat.setSelected(true);
        txt_tacGia.setSelected(true);
        Bundle bl = getArguments();

        if (bl!=null)
        {
            String ten = bl.getString("TenBaiHat");
            anh = bl.getString("Anh");
            String tacgia = bl.getString("TacGia");
            url = bl.getString("url");

            txt_baiHat.setText(ten);
            txt_tacGia.setText(tacgia);

            Picasso.get().load(anh).placeholder(R.drawable.loading).error(R.drawable.warning).into(img_anh);



        }
    }
    private boolean isPlaying() {
        return MusicServiceHelper.isPlaying(); // hàm static hỗ trợ kiểm tra từ service
    }
    @Override
    public void onClick(View v) {
            PlayMusicFragment playlist = (PlayMusicFragment) getParentFragmentManager().findFragmentByTag("PlayMusic");
            if (playlist != null) {
                ImageButton img = playlist.btn_pause;
                if(v.getId()==R.id.vm_stop)
                {
                    if(isPlaying())
                    {
                        Intent intent = new Intent(requireContext(), MusicService.class);
                        intent.setAction("PAUSE");
                        requireContext().startService(intent);
                        MusicServiceHelper.setPhat(false);
                    }
                    else{
                        Intent serviceIntent = new Intent(requireContext(), MusicService.class);
                        serviceIntent.setAction("RESUME");
//                        serviceIntent.putExtra("url", url);
//                        serviceIntent.putExtra("tenbai", txt_baiHat.getText().toString());
//                        serviceIntent.putExtra("tacgia", txt_tacGia.getText().toString());
//                        serviceIntent.putExtra("anh", anh);
                        requireContext().startService(serviceIntent);
                        MusicServiceHelper.setPhat(true);

                    }
                }
                if(v.getId()==R.id.layout_view_music)
                {
                    FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                    fr.show(playlist);
                    fr.hide(this);
                    fr.commit();
                    ((MainActivity) requireActivity()).bottomNav.setVisibility(v.GONE);
                }
            }
    }
}