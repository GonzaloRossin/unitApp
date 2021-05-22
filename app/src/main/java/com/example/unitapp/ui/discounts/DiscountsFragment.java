package com.example.unitapp.ui.discounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.unitapp.R;
import com.example.unitapp.databinding.FragmentDiscountsBinding;

public class DiscountsFragment extends Fragment {

    private DiscountsViewModel discountsViewModel;
    private FragmentDiscountsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        discountsViewModel =
                new ViewModelProvider(this).get(DiscountsViewModel.class);

        binding = FragmentDiscountsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDiscounts;
        discountsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}