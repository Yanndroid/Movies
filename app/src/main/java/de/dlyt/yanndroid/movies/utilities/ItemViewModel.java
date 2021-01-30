package de.dlyt.yanndroid.movies.utilities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemViewModel extends ViewModel {

    private final MutableLiveData<HashMap<String, String>> filters = new MutableLiveData<>();

    public void setHashMap(HashMap<String, String> hashMap) {
        filters.setValue(hashMap);
    }

    public LiveData<HashMap<String, String>> getHashmap() {
        return filters;
    }


}