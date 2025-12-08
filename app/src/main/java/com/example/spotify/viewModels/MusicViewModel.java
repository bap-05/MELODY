package com.example.spotify.viewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotify.models.Music;
import com.example.spotify.Repository.MusicRepository;
import com.example.spotify.models.Radio;
import com.example.spotify.models.UploadMusic;

import java.util.List;

public class MusicViewModel extends ViewModel {

    public int kt = 3;
    private final MusicRepository repository = new MusicRepository();
    public static MutableLiveData<Boolean> isPreparing = new MutableLiveData<>(false);
    public static MutableLiveData<Integer> ktra= new MutableLiveData<>(0);
    private final MutableLiveData<List<Music>> musicList = new MutableLiveData<>();
    private final MutableLiveData<List<Music>> musicList2 = new MutableLiveData<>();
    private final MutableLiveData<List<Music>> musicList3 = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Music>> listMusic = new MutableLiveData<>();

    public MutableLiveData<List<Music>> getListMusic() {
        return listMusic;
    }

    public void setListMusic(List<Music> listMusic) {
        this.listMusic.postValue(listMusic);
    }

    public static MutableLiveData<Boolean> getIsPreparing() {
        return isPreparing;
    }

    public static MutableLiveData<Integer> getKtra() {
        return ktra;
    }

    public static void setKtra(Integer ktra) {
        MusicViewModel.ktra.setValue(ktra);
    }

    public static void setIsPreparing(Boolean isPreparing1) {
        isPreparing.setValue(isPreparing1);
    }

    public MutableLiveData<List<Music>> getMusicList3() {
        return musicList3;
    }

    public LiveData<List<Music>> getMusicList() {
        return musicList;
    }

    public LiveData<List<Music>> getMusicList2() {
        return musicList2;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadMusic() {


        if (musicList.getValue() != null && !musicList.getValue().isEmpty()) {
            kt=0;
            return; // => không gọi API lại
        }
        else{
            repository.fetchMusic(musicList, errorMessage);
            kt = repository.kt;
        }

    }

    public void getallmusic(){
        repository.getallmusic(musicList2, errorMessage);
    }
    public void loadMusicRadio(Radio radio) {
            repository.getmusicradio(musicList3, errorMessage,radio);
    }
    public void search(String query)
    {
        repository.search(listMusic,errorMessage,query);
    }
    public void insert(UploadMusic uploadMusic)
    {
        repository.create(uploadMusic);
    }
}
