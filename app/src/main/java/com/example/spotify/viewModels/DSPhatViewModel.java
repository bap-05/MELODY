package com.example.spotify.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotify.Repository.DSPhatRepository;
import com.example.spotify.models.DSPhat;

import java.util.List;

public class DSPhatViewModel extends ViewModel {
    private DSPhatRepository dsPhatRepository = new DSPhatRepository();
    private MutableLiveData<List<DSPhat>> DSPhats = new MutableLiveData<>();
    private MutableLiveData<String>err= new MutableLiveData<>();

    public MutableLiveData<List<DSPhat>> getDSPhats() {
        return DSPhats;
    }

    public MutableLiveData<String> getErr() {
        return err;
    }
    public void dsPhat(String ma){
        dsPhatRepository.DSPhat(DSPhats,err,ma);
    }
}
