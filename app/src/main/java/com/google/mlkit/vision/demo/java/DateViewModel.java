package com.google.mlkit.vision.demo.java;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DateViewModel extends ViewModel {
    private MutableLiveData<String> date = new MutableLiveData<String>();

    public String getDate() {
        return date.getValue();
    }

    public void setDate(String date) {
        this.date.setValue(date);
    }
}