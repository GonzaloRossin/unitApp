package com.example.unitapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unitapp.R;

import org.jetbrains.annotations.NotNull;

public class WelcomeFragment extends Fragment {

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button signinBtn = rootView.findViewById(R.id.signin);
        Button registerBtn = rootView.findViewById(R.id.register);

        signinBtn.setOnClickListener((v)-> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment());
        });

        registerBtn.setOnClickListener((v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToRegisterFragment());
        }));
        return rootView;
    }
}
