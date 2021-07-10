package com.example.unitapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.activities.MainActivity;
import com.example.unitapp.api.model.Error;
import com.example.unitapp.api.model.RegisterCredentials;
import com.example.unitapp.databinding.FragmentRegisterBinding;
import com.example.unitapp.repository.Resource;
import com.example.unitapp.repository.Status;
import com.example.unitapp.viewModel.UserViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        registerBtn.setOnClickListener(this::tryRegister);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    private void tryRegister(View v) {
        UnitApp app = ((UnitApp)requireActivity().getApplication());
        Integer lPhone = Integer.valueOf(this.phone.getText().toString());
        RegisterCredentials credentials = new RegisterCredentials(username.getText().toString(), password.getText().toString(), lPhone);
        app.getUserRepository().register(credentials).observe(getViewLifecycleOwner(), r -> {
            if(r.getStatus() == Status.SUCCESS) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireActivity().finish();
            } else {
                defaultResourceHandler(r);
            }
        });
    }

    private void defaultResourceHandler(Resource<?> resource) {
        switch (resource.getStatus()) {
            case LOADING:
                break;
            case ERROR:
                Error error = resource.getError();
                Toast.makeText(requireContext(), Objects.requireNonNull(error).getCode() + ": " + error.getDescription(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
