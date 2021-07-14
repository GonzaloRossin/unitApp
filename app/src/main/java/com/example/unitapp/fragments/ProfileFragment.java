package com.example.unitapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.activities.MainActivity;
import com.example.unitapp.databinding.FragmentProfileBinding;
import com.example.unitapp.databinding.FragmentRegisterBinding;
import com.example.unitapp.repository.Status;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentProfileBinding binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        UnitApp app = ((UnitApp)requireActivity().getApplication());
        app.getUserRepository().getCurrentUser().observe(getViewLifecycleOwner(), r -> {
            if(r.getStatus() == Status.SUCCESS) {
                binding.profilePhone.setText(String.valueOf(Objects.requireNonNull(r.getData()).getPhone()));
                binding.profileEmail.setText(r.getData().getEmail());
                binding.cabify.setText(new StringBuilder().append(getString(R.string.link)).append(" Cabify").toString());
                binding.uber.setText(new StringBuilder().append(getString(R.string.link)).append(" Uber").toString());
            }
        });


        return view;
    }
}