package com.example.unitapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.activities.LoadingActivity;
import com.example.unitapp.databinding.FragmentProfileBinding;
import com.example.unitapp.repository.Status;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class ProfileFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentProfileBinding binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        UnitApp app = ((UnitApp) requireActivity().getApplication());
        
        binding.logOut.setOnClickListener(v -> {
            app.getPreferences().setCabifyToken(-1);
            app.getPreferences().setUberToken(-1);
            app.getPreferences().setAuthToken(null);
            Intent intent = new Intent(requireContext(), LoadingActivity.class);
            startActivity(intent);
        });
        app.getUserRepository().getCurrentUser().observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                binding.profilePhone.setText(String.valueOf(Objects.requireNonNull(r.getData()).getPhone()));
                binding.profileEmail.setText(r.getData().getEmail());
            }
        });


        return view;
    }

}