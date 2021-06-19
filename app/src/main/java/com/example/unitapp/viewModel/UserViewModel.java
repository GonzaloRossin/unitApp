package com.example.unitapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;

public class UserViewModel extends AndroidViewModel {
    //aca van todos los metodos para conectarnos con la base de datos

    public UserViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
