package com.example.spotify.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotify.Repository.RadioRepository;
import com.example.spotify.models.Radio;

import java.util.ArrayList;
import java.util.List;

public class RadioViewModel extends ViewModel {


    private final RadioRepository repository = new RadioRepository();
    private final MutableLiveData<List<Radio>> radioList = new MutableLiveData<>();
    private static MutableLiveData<Radio> rd = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public static MutableLiveData<Radio> getRd() {
        return rd;
    }

    public static void setRd(Radio r) {
        rd.setValue(r);
    }

    public MutableLiveData<List<Radio>> getRadioList() {
        return radioList;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public void loadRadio(){
        if (radioList.getValue() != null && !radioList.getValue().isEmpty()) {
            return;
        }

        repository.top5Radio(radioList,errorMessage);
    }
//    private List<Radio> rdo = new ArrayList<>();
//    public RadioViewModel() {
//    }
//
//    public List<Radio> getrdo() {
//        return rdo;
//    }
//
//    public List setView(){
//        Radio rd1 = new Radio("TLinh, Sơn Tùng MTP, Obito", "Obito","https://i.scdn.co/image/ab67616d00001e02cb2a3066584a339e09508520","https://i1.sndcdn.com/artworks-fqL73ggcxCeQtgsf-wQqmRQ-t1080x1080.jpg","https://i.scdn.co/image/ab67616d00001e0203aeb634b34fed42641718a2");
//        Radio rd2 = new Radio("TLinh, Sơn Tùng MTP, Obito", "Obito","https://i.scdn.co/image/ab67616d00001e02cb2a3066584a339e09508520","https://i1.sndcdn.com/artworks-fqL73ggcxCeQtgsf-wQqmRQ-t1080x1080.jpg","https://i.scdn.co/image/ab67616d00001e0203aeb634b34fed42641718a2");
//        Radio rd3= new Radio("TLinh, Sơn Tùng MTP, Obito", "Obito","https://i.scdn.co/image/ab67616d00001e02cb2a3066584a339e09508520","https://i1.sndcdn.com/artworks-fqL73ggcxCeQtgsf-wQqmRQ-t1080x1080.jpg","https://i.scdn.co/image/ab67616d00001e0203aeb634b34fed42641718a2");
//
//        rdo.add(rd1);
//        rdo.add(rd2);
//        rdo.add(rd3);
//
//        return rdo;
//
//    }
}
