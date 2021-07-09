package com.example.unitapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.unitapp.R;
import com.example.unitapp.activities.MainActivity;
import com.example.unitapp.databinding.FragmentRegisterBinding;
import com.example.unitapp.viewModel.UserViewModel;

import org.jetbrains.annotations.NotNull;

public class RegisterFragment extends Fragment {
    private EditText username;
    private EditText password;
    private EditText phone;
    private UserViewModel viewModel;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        FragmentRegisterBinding binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        username = binding.username;
        password = binding.password;
        phone = binding.phone;

        View view = binding.getRoot();
        Button registerBtn = view.findViewById(R.id.signin);
        registerBtn.setOnClickListener(v -> tryRegister());

        return view;

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    private void tryRegister(){
        //hacer todo el register

        //chequear token... Lo pongo de una para poder entrar mientras
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requireActivity().finish();
    }
}
