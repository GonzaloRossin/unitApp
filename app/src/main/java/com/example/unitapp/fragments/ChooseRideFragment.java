package com.example.unitapp.fragments;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.api.model.Error;
import com.example.unitapp.repository.Resource;
import com.example.unitapp.repository.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

import kotlin.Unit;


public class ChooseRideFragment extends Fragment {
    Place destination;
    Location currentLocation;
    LatLng destCoordinates;
    public ChooseRideFragment(Location location, Place dest, LatLng targetLocation) {
        currentLocation = location;
        destination = dest;
        destCoordinates = targetLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_ride, container, false);
        final MaterialCardView unit_x = view.findViewById(R.id.unit_x);
        final MaterialCardView unit_xl = view.findViewById(R.id.unit_xl);
        final MaterialCardView unit_flash = view.findViewById(R.id.unit_flash);
        final MaterialCardView uber_cardView = view.findViewById(R.id.uber_cardview);
        final MaterialCardView cabify_cardView = view.findViewById(R.id.cabify_cardview);
        unit_x.setOnClickListener(v -> {
            unit_x.toggle();
            uber_cardView.setVisibility(View.VISIBLE);
            cabify_cardView.setVisibility(View.VISIBLE);
            if(unit_xl.isChecked()) unit_xl.toggle();
            if(unit_flash.isChecked()) unit_flash.toggle();
        });
        unit_xl.setOnClickListener(v -> {
            unit_xl.toggle();
            uber_cardView.setVisibility(View.VISIBLE);
            cabify_cardView.setVisibility(View.VISIBLE);
            if(unit_x.isChecked()) unit_x.toggle();
            if(unit_flash.isChecked()) unit_flash.toggle();
        });
        unit_flash.setOnClickListener(v -> {
            unit_flash.toggle();
            uber_cardView.setVisibility(View.VISIBLE);
            cabify_cardView.setVisibility(View.VISIBLE);
            if(unit_x.isChecked()) unit_x.toggle();
            if(unit_xl.isChecked()) unit_xl.toggle();
        });
        TextView fromAddress = view.findViewById(R.id.fromAddress);
        TextView toAddress = view.findViewById(R.id.toAddress);
        toAddress.setText(destination.getAddress());
        UnitApp app = ((UnitApp)requireActivity().getApplication());
        app.getRideRepository().getAvailableDrivers(currentLocation.getLatitude(),
                destCoordinates.latitude,
                currentLocation.getLongitude(),
                destCoordinates.longitude)
                .observe(getViewLifecycleOwner(), r -> {
                    if(r.getStatus() == Status.SUCCESS) {
                        Log.d("TEST","TEST");
                    } else {
                        defaultResourceHandler(r);
                    }
                });
        return view;
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
