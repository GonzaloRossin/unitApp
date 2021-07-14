package com.example.unitapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.unitapp.R;
import com.example.unitapp.databinding.FragmentDiscountsBinding;

import org.jetbrains.annotations.NotNull;


public class DiscountsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDiscountsBinding binding = FragmentDiscountsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        CardView disc1 = view.findViewById(R.id.disc1);
        CardView disc2 = view.findViewById(R.id.disc2);
        CardView disc3 = view.findViewById(R.id.disc3);

        disc1.setOnClickListener(this::toast);
        disc2.setOnClickListener(this::toast);
        disc3.setOnClickListener(this::toast);

        return view;
    }

    private void toast(View view) {
        Toast.makeText(requireContext(),"Descuento aplicado!",Toast.LENGTH_SHORT).show();
    }


}