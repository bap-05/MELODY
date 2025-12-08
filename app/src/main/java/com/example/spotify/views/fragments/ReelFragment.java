package com.example.spotify.views.fragments;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spotify.R;
import com.example.spotify.adapter.ReelAdapter;
import com.example.spotify.models.Reel;

import java.util.ArrayList;
import java.util.List;

public class ReelFragment extends Fragment {
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    private ExoPlayer exoPlayer;
    private ReelAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reel, container, false);
        v.setKeepScreenOn(true);
        ViewPager2 viewPager = v.findViewById(R.id.viewPager);
        exoPlayer = new ExoPlayer.Builder(v.getContext()).build();

        List<Reel> list = new ArrayList<>();
        list.add(new Reel("https://drive.google.com/uc?export=download&id=1jd101aHo8KB22eOdLhIcEKrXLmrWWM-1"));
        list.add(new Reel("https://drive.google.com/uc?export=download&id=1fEvhK-PxJrDedIUEq54TJyexivn7Llr9"));
        list.add(new Reel("https://drive.google.com/uc?export=download&id=1r7zPX4GSz4kzkNFrO7gH_YqYxJLrUKuM"));

        adapter = new ReelAdapter(v.getContext(), list, exoPlayer);
        viewPager.setAdapter(adapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        // 🎯 Khi chuyển sang trang khác
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

                if (viewHolder instanceof ReelAdapter.ReelViewHolder) {
                    ReelAdapter.ReelViewHolder reelHolder = (ReelAdapter.ReelViewHolder) viewHolder;

                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        View child = recyclerView.getChildAt(i);
                        RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                        if (vh instanceof ReelAdapter.ReelViewHolder && vh != viewHolder) {
                            ((ReelAdapter.ReelViewHolder) vh).playerView.setPlayer(null);
                        }
                    }

                    // 🔹 Gắn player vào PlayerView hiện tại
                    reelHolder.playerView.setPlayer(exoPlayer);

                    // 🔹 Dừng video cũ, clear media, set video mới
                    adapter.playVideoAtPosition(position);
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

        afChangeListener = focusChange -> {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (exoPlayer != null && !exoPlayer.isPlaying()) {
                        exoPlayer.play();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (exoPlayer != null && exoPlayer.isPlaying()) {
                        exoPlayer.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (exoPlayer != null && exoPlayer.isPlaying()) {
                        exoPlayer.setVolume(0.3f);
                    }
                    break;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(afChangeListener)
                    .build();

            audioManager.requestAudioFocus(focusRequest);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (audioManager != null && afChangeListener != null) {
            audioManager.abandonAudioFocus(afChangeListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (audioManager != null && afChangeListener != null) {
            audioManager.abandonAudioFocus(afChangeListener);
        }
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
