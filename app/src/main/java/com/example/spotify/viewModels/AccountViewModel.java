package com.example.spotify.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotify.Repository.AccountRepository;
import com.example.spotify.models.Account;

public class AccountViewModel extends ViewModel {
    private static MutableLiveData<Account> acc = new MutableLiveData();

    public static LiveData<Account> getAcc() {
        return acc;
    }

    public static void setAcc(Account ac) {
        acc.setValue(ac);
    }

    private AccountRepository accountRepository = new AccountRepository();

    public void save(){
        accountRepository.savetk(acc);
    }
}
