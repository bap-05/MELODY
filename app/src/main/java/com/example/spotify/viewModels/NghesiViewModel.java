package com.example.spotify.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotify.Repository.NgheSiRepository;
import com.example.spotify.models.Music;
import com.example.spotify.models.Nghesi;

import java.util.ArrayList;
import java.util.List;

public class NghesiViewModel extends ViewModel {
   private MutableLiveData<List<Nghesi>> nghesilist = new MutableLiveData<>();
    private MutableLiveData<List<Nghesi>> nghesilis2 = new MutableLiveData<>();
   private MutableLiveData<List<Music>> lms = new MutableLiveData<>();
    private MutableLiveData<String> err = new MutableLiveData<>();
    private MutableLiveData<Nghesi> nghesi = new MutableLiveData<>();
    private NgheSiRepository repository = new NgheSiRepository();

    public MutableLiveData<List<Nghesi>> getNghesilist() {
        return nghesilist;
    }

    public MutableLiveData<Nghesi> getNghesi() {
        return nghesi;
    }

    public MutableLiveData<List<Nghesi>> getNghesilis2() {
        return nghesilis2;
    }

    public void setNghesi(Nghesi nghesi) {
        this.nghesi.setValue(nghesi);
    }

    public MutableLiveData<List<Music>> getLms() {
        return lms;
    }

    public MutableLiveData<String> getErr() {
        return err;
    }
    public void loadNS()
    {
        if(nghesilist.getValue()!=null && nghesilist.getValue().isEmpty())
            return;
        repository.getNS(nghesilist,err);
    }

    public void loadMS(){
        if(nghesilist.getValue()!=null && nghesilist.getValue().isEmpty())
            return;
        repository.getms(lms,err,nghesi.getValue());
    }
    public void search(String query)
    {
        repository.search_ns(nghesilis2,err,query);
    }
    public void themNS(Nghesi ns)
    {
        repository.themNS(ns);
    }
    //    public void setLns(List<Nghesi> lns) {
//        this.lns = lns;
//    }
//    public List addView(){
////        Nghesi ns1 = new Nghesi("Tlinh","https://i.scdn.co/image/ab67616100005174230e62752ca87da1d85d0445");
////        Nghesi ns2 = new Nghesi("Sơn Tùng MTP","https://i.scdn.co/image/ab676161000051745a79a6ca8c60e4ec1440be53");
////        Nghesi ns3 = new Nghesi("HIEUTHUHAI","https://i.scdn.co/image/ab6761610000517421942907035a43a2d118c55c");
////        lns.add(ns1);
////        lns.add(ns2);
////        lns.add(ns3);
////        return lns;
//    }
}
