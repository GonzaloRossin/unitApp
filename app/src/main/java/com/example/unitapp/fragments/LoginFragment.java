package com.example.unitapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.unitapp.classes.UserInfo;
import com.example.unitapp.databinding.FragmentLoginBinding;
import com.example.unitapp.viewModel.UserViewModel;

import org.jetbrains.annotations.NotNull;

public class LoginFragment extends Fragment {

    private EditText username;
    private EditText password;
    private UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        FragmentLoginBinding binding = FragmentLoginBinding.inflate(getLayoutInflater());
        username = binding.username;
        password = binding.password;

        View view = binding.getRoot();
        Button signinBtn = view.findViewById(R.id.signin);
        signinBtn.setOnClickListener(v -> trySignin());

        return view;

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    private void trySignin(){
        //hacer todo el signIn

        //chequear token... Lo pongo de una para poder entrar mientras
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requireActivity().finish();
    }
}
