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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.activities.MainActivity;
import com.example.unitapp.api.model.Error;
import com.example.unitapp.api.model.LoginCredentials;
import com.example.unitapp.classes.UserInfo;
import com.example.unitapp.databinding.FragmentLoginBinding;
import com.example.unitapp.repository.Resource;
import com.example.unitapp.repository.Status;
import com.example.unitapp.viewModel.UserViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        signinBtn.setOnClickListener(this::trySignin);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    private void trySignin(View v){
        UnitApp app = ((UnitApp)requireActivity().getApplication());
        LoginCredentials credentials = new LoginCredentials(username.getText().toString(), password.getText().toString());
        app.getUserRepository().login(credentials).observe(getViewLifecycleOwner(), r -> {
            if(r.getStatus() == Status.SUCCESS) {
                requireActivity().findViewById(R.id.login_progress_bar).setVisibility(View.GONE);
                app.getPreferences().setAuthToken(Objects.requireNonNull(r.getData()).getToken());
                app.getPreferences().setCabifyToken(r.getData().getCabifyId());
                app.getPreferences().setUberToken(r.getData().getUberId());
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
                requireActivity().findViewById(R.id.login_progress_bar).setVisibility(View.VISIBLE);
                break;
            case ERROR:
                requireActivity().findViewById(R.id.login_progress_bar).setVisibility(View.GONE);
                Error error = resource.getError();
                Toast.makeText(requireContext(), Objects.requireNonNull(error).getCode() + ": " + error.getDescription(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
