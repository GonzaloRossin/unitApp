package com.example.unitapp.fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.unitapp.R;
import com.google.android.libraries.places.api.model.Place;


public class ChooseRideFragment extends Fragment {
    Place Destination;
    Location CurrentLocaton;

    public ChooseRideFragment(Location location,Place destination) {
        CurrentLocaton=location;
        Destination = destination;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_ride, container, false);
        TextView fromAddress= view.findViewById(R.id.fromAddress);
        TextView toAddress=view.findViewById(R.id.toAddress);
        toAddress.setText(Destination.getAddress());
        return view;
    }
}
