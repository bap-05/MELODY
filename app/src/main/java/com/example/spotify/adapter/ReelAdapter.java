package com.example.spotify.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify.R;
import com.example.spotify.models.Reel;

import java.util.List;

public class ReelAdapter extends RecyclerView.Adapter<ReelAdapter.ReelViewHolder> {

    private Context context;
    private List<Reel> videoList;
    public static ExoPlayer exoPlayer;

    public ReelAdapter(Context context, List<Reel> videoList, ExoPlayer exoPlayer) {
        this.context = context;
        this.videoList = videoList;
        this.exoPlayer = exoPlayer;
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reel, parent, false);
        return new ReelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        // Không set player ở đây — ta sẽ set trong scroll listener để tránh lỗi
        holder.bind(videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ReelViewHolder extends RecyclerView.ViewHolder {
        public PlayerView playerView;
        String videoUrl;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(exoPlayer.isPlaying())
                        exoPlayer.pause();
                    else
                        exoPlayer.play();
                }
            });
        }

        void bind(Reel model) {
            videoUrl = model.getUrl();
        }
    }

    // Hàm gắn ExoPlayer vào item hiện tại
    public void playVideoAtPosition(int position) {
        if (position < 0 || position >= videoList.size())
            return;
        Reel video = videoList.get(position);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(video.getUrl()));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
    }
}
