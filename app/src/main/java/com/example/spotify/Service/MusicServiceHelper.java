package com.example.spotify.Service;

import android.media.MediaPlayer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotify.models.Music;

public class MusicServiceHelper {
    private static final MutableLiveData<Music> currentSong = new MutableLiveData<>();
    private  static MutableLiveData<Boolean> phat = new MutableLiveData<>(true);
    private static MutableLiveData<Music> music = new MutableLiveData<>();
    public static MutableLiveData<Boolean> getPhat() {
        return phat;
    }

    public static void setPhat(Boolean phat1) {
        phat.setValue(phat1);
    }

    public static void setCurrentSong(Music music) {
        currentSong.postValue(music);
    }

    public static LiveData<Music> getCurrentSong() {
        return currentSong;
    }
    private static MediaPlayer player;
    private static boolean isPlaying = false;

    public static MutableLiveData<Music> getMusic() {
        return music;
    }

    public static void setMusic(Music music1) {
       music.setValue(music1);
    }

    public static void setPlayer(MediaPlayer mediaPlayer) {
        player = mediaPlayer;
    }

    public static MediaPlayer getPlayer() {
        return player;
    }

    public static void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public static boolean isPlaying() {
        return player != null && isPlaying && player.isPlaying();
    }

//    public static int getCurrentPosition() {
//        return player != null ? player.getCurrentPosition() : 0;
//    }
//
//    public static int getDuration() {
//        return player != null ? player.getDuration() : 0;
//    }
//
//    public static void pause() {
//        if (player != null && player.isPlaying()) {
//            player.pause();
//            isPlaying = false;
//        }
//    }

    public static void resume() {
        if (player != null) {
            player.start();
            isPlaying = true;
        }
    }
}
