package com.example.unitapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> name;
    private MutableLiveData<String> last_name;

    public ProfileViewModel() {
        name = new MutableLiveData<>();
        name.setValue("Mi nombre");
        last_name = new MutableLiveData<>();
        last_name.setValue("Mi apellido");
    }

    public LiveData<String> getName() {
        return name;
    }
    public LiveData<String> getLastName() {
        return last_name;
    }

}