package com.example.unitapp.fragments;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.api.model.Driver;
import com.example.unitapp.api.model.Error;
import com.example.unitapp.repository.Resource;
import com.example.unitapp.repository.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import kotlin.Unit;


public class ChooseRideFragment extends Fragment {
    Place destination;
    Location currentLocation;
    LatLng destCoordinates;
    Optional<Driver> uberDriver;
    Optional<Driver> cabifyDriver;

    public ChooseRideFragment(Location location, Place dest, LatLng targetLocation) {
        currentLocation = location;
        destination = dest;
        destCoordinates = targetLocation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setPriceTags(AtomicReference<List<Driver>> drivers,
                              int minCapacity,
                              MaterialCardView uber_cardView,
                              MaterialCardView cabify_cardView,
                              TextView uberPrice,
                              TextView cabifyPrice) {
        uberDriver = drivers.get()
                .stream()
                .filter(driver -> driver.getServiceId() == 1 && driver.getCapacity() == minCapacity)
                .min(Comparator.comparing(Driver::getEstimatedPrice));
        cabifyDriver = drivers.get()
                .stream()
                .filter(driver -> driver.getServiceId() == 2 && driver.getCapacity() == minCapacity)
                .min(Comparator.comparing(Driver::getEstimatedPrice));

        if(uberDriver.isPresent()) {
            uber_cardView.setVisibility(View.VISIBLE);
            uberPrice.setText(String.valueOf(uberDriver.get().getEstimatedPrice()));
        } else {
            uber_cardView.setVisibility(View.GONE);
        }

        if(cabifyDriver.isPresent()) {
            cabify_cardView.setVisibility(View.VISIBLE);
            cabifyPrice.setText(String.valueOf(cabifyDriver.get().getEstimatedPrice()));
        } else {
            cabify_cardView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_ride, container, false);
        final MaterialCardView unit_x = view.findViewById(R.id.unit_x);
        final MaterialCardView unit_xl = view.findViewById(R.id.unit_xl);
        final MaterialCardView unit_flash = view.findViewById(R.id.unit_flash);
        final MaterialCardView uber_cardView = view.findViewById(R.id.uber_cardview);
        final MaterialCardView cabify_cardView = view.findViewById(R.id.cabify_cardview);
        final TextView confirm_ride = view.findViewById(R.id.confirm_ride);
        final TextView uberPrice = view.findViewById(R.id.option1_price);
        final TextView cabifyPrice = view.findViewById(R.id.option2_price);
        final MaterialButton confirmEFAB = view.findViewById(R.id.request_driver_btn);
        AtomicReference<List<Driver>> drivers = new AtomicReference<>();
        uber_cardView.setOnClickListener(v -> {
            uber_cardView.toggle();
            if(uber_cardView.isChecked()) {
                confirmEFAB.setVisibility(View.VISIBLE);
            } else {
                confirmEFAB.setVisibility(View.INVISIBLE);
            }
            if(cabify_cardView.isChecked()) cabify_cardView.toggle();
        });

        cabify_cardView.setOnClickListener(v -> {
            cabify_cardView.toggle();
            if(cabify_cardView.isChecked()) {
                confirmEFAB.setVisibility(View.VISIBLE);
            } else {
                confirmEFAB.setVisibility(View.INVISIBLE);
            }
            if(uber_cardView.isChecked()) uber_cardView.toggle();
        });

        confirmEFAB.setOnClickListener(v -> {
            Driver selectedDriver = uberDriver.orElseGet(() -> cabifyDriver.get());
            HomeFragment homeFragment = new HomeFragment(true, selectedDriver);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
            transaction.replace(R.id.mainNavFragment, homeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        unit_x.setOnClickListener(v -> {
            unit_x.toggle();
            if(unit_x.isChecked()) {
                uber_cardView.setVisibility(View.VISIBLE);
                cabify_cardView.setVisibility(View.VISIBLE);
                confirm_ride.setVisibility(View.VISIBLE);
                setPriceTags(drivers, 3, uber_cardView, cabify_cardView, uberPrice, cabifyPrice);

            } else {
                uber_cardView.setVisibility(View.INVISIBLE);
                cabify_cardView.setVisibility(View.INVISIBLE);
                confirm_ride.setVisibility(View.INVISIBLE);
            }
            if(unit_xl.isChecked()) unit_xl.toggle();
            if(unit_flash.isChecked()) unit_flash.toggle();
        });
        unit_xl.setOnClickListener(v -> {
            unit_xl.toggle();
            if(unit_xl.isChecked()) {
                uber_cardView.setVisibility(View.VISIBLE);
                cabify_cardView.setVisibility(View.VISIBLE);
                confirm_ride.setVisibility(View.VISIBLE);
                setPriceTags(drivers, 6, uber_cardView, cabify_cardView, uberPrice, cabifyPrice);
            } else {
                uber_cardView.setVisibility(View.INVISIBLE);
                cabify_cardView.setVisibility(View.INVISIBLE);
                confirm_ride.setVisibility(View.INVISIBLE);
            }
            if(unit_x.isChecked()) unit_x.toggle();
            if(unit_flash.isChecked()) unit_flash.toggle();
        });
        unit_flash.setOnClickListener(v -> {
            unit_flash.toggle();
            if(unit_flash.isChecked()) {
                uber_cardView.setVisibility(View.VISIBLE);
                cabify_cardView.setVisibility(View.VISIBLE);
                confirm_ride.setVisibility(View.VISIBLE);
                setPriceTags(drivers, 2, uber_cardView, cabify_cardView, uberPrice, cabifyPrice);
            } else {
                uber_cardView.setVisibility(View.INVISIBLE);
                cabify_cardView.setVisibility(View.INVISIBLE);
                confirm_ride.setVisibility(View.INVISIBLE);
            }
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
                        requireActivity().findViewById(R.id.choose_ride_bar).setVisibility(View.GONE);
                        requireActivity().findViewById(R.id.choose_ride).setVisibility(View.VISIBLE);
                        drivers.set(Objects.requireNonNull(r.getData()).getDrivers());
                        int maxCapacity = 0;
                        for(int i = 0; i < drivers.get().size(); i++) {
                            Driver driver = drivers.get().get(i);
                            if(driver.getCapacity() > maxCapacity) maxCapacity = driver.getCapacity();
                        }
                        if(maxCapacity == 2) unit_flash.setVisibility(View.VISIBLE);
                        if(maxCapacity == 3) unit_x.setVisibility(View.VISIBLE);
                        if(maxCapacity == 6) unit_xl.setVisibility(View.VISIBLE);

                    } else {
                        defaultResourceHandler(r);
                    }
                });

        return view;
    }

    private void defaultResourceHandler(Resource<?> resource) {
        switch (resource.getStatus()) {
            case LOADING:
                requireActivity().findViewById(R.id.choose_ride_bar).setVisibility(View.VISIBLE);
                break;
            case ERROR:
                requireActivity().findViewById(R.id.choose_ride_bar).setVisibility(View.GONE);
                Error error = resource.getError();
                Toast.makeText(requireContext(), Objects.requireNonNull(error).getCode() + ": " + error.getDescription(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
