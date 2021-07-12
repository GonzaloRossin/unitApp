package com.example.unitapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.api.model.DirectionResponse;
import com.example.unitapp.api.model.Leg;
import com.example.unitapp.api.model.Route;
import com.example.unitapp.api.model.Step;
import com.example.unitapp.model.HaversineDistance;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.maps.android.PolyUtil;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.example.unitapp.utils.Constants.MAPVIEW_BUNDLE_KEY;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    FusedLocationProviderClient fusedLocationClient;
    ExtendedFloatingActionButton confirmButton;
    private static final String TAG = "info:";
    private static final String KEY = "AIzaSyAMElDromNlk946AR6VTHSpkOvaV84Kk2Y";
    Place endAddress = null;
    GoogleMap appMap;
    PlacesClient placesClient;
    LocationRequest locationRequest;


    private void updateMap() {
        /*if (endAddress != null) {
            if(checkPermission()){
                fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(),location -> {
                    if (location!=null){
                        appMap.clear();
                        appMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(endAddress.getLatLng())));
                        if(HaversineDistance.distance(new LatLng(location.getLatitude(),location.getLongitude()), endAddress.getLatLng())<=30){
                            Toast.makeText(this.requireContext(),"You are on destination",Toast.LENGTH_LONG).show();
                            stopLocationUpdates();
                        }else{
                            getDirections();
                        }
                    }
                });
            }
        }*/
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext());
        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(), "AIzaSyAMElDromNlk946AR6VTHSpkOvaV84Kk2Y");
            placesClient = Places.createClient(this.requireContext());
        }
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * 90);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mMapView = view.findViewById(R.id.mapView2);
        initGoogleMap(savedInstanceState);
        confirmButton = view.findViewById(R.id.floating_action_button);
        confirmButton.setOnClickListener(v -> {
            if (endAddress != null && checkPermission()) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                    if (location != null) {
                        ChooseRideFragment chooseRideFragment = new ChooseRideFragment(location, endAddress, endAddress.getLatLng());
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
                        transaction.replace(R.id.container, chooseRideFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            } else {
                Toast.makeText(this.requireContext(), "Please enter a destination", Toast.LENGTH_SHORT).show();
            }
        });
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteFragment.setCountries("AR");
        autocompleteFragment.setHint("Where are you going?");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (endAddress != null) {
                    appMap.clear();
                }
                endAddress = place;
                if (checkPermission()) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(HomeFragment.super.requireActivity(), location -> {
                        if (location != null) {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(Objects.requireNonNull(endAddress.getLatLng()));
                            builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                            appMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(endAddress.getLatLng())));
                            getDirections();
                            appMap.animateCamera(cameraUpdate);
                        }
                    });
                }

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        return view;
    }

    public void getDirections() {
        if (checkPermission()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                if (location != null) {
                    UnitApp app = ((UnitApp) requireActivity().getApplication());
                    String origin = location.getLatitude() + "," + location.getLongitude();
                    String destination = Objects.requireNonNull(endAddress.getLatLng()).latitude + "," + endAddress.getLatLng().longitude;
                    app.getMapsRepository().getDirections(origin, destination, KEY).observe(getViewLifecycleOwner(), r -> {
                        if (r.getStatus() == com.example.unitapp.repository.Status.SUCCESS) {
                            DirectionResponse directionResponse = r.getData();
                            Route mainRoute = Objects.requireNonNull(directionResponse).getRoutes().get(0);
                            Leg mainLeg = mainRoute.getLegs().get(0);
                            List<Step> steps = mainLeg.getSteps();
                            for (int i = 0; i < steps.size(); i++) {
                                String polylinePoint = steps.get(i).getPolyline().getPoints();
                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.BLUE);
                                polylineOptions.width(10);
                                polylineOptions.addAll(PolyUtil.decode(polylinePoint));
                                appMap.addPolyline(polylineOptions);
                            }
                        }
                    });
                }
            });
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        startLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(@NotNull GoogleMap map) {
        appMap = map;
        if (checkPermission()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                if (location != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17);
                    map.animateCamera(cameraUpdate);
                }
            });
            map.setMyLocationEnabled(true);
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            updateMap();
        }
    };

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationUpdates() {
        if (checkPermission()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}