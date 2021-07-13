package com.example.unitapp.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.api.model.DirectionResponse;
import com.example.unitapp.api.model.Driver;
import com.example.unitapp.api.model.Leg;
import com.example.unitapp.api.model.Route;
import com.example.unitapp.model.BeginJourneyEvent;
import com.example.unitapp.model.CurrentJourneyEvent;
import com.example.unitapp.model.EndJourneyEvent;
import com.example.unitapp.model.HaversineDistance;
import com.example.unitapp.utils.JourneyEventBus;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.PolyUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import static com.example.unitapp.utils.Constants.MAPVIEW_BUNDLE_KEY;
import static com.google.android.gms.maps.model.JointType.ROUND;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    FusedLocationProviderClient fusedLocationClient;
    ExtendedFloatingActionButton confirmButton;
    private static final String TAG = "info:";
    private static final String KEY = "AIzaSyAMElDromNlk946AR6VTHSpkOvaV84Kk2Y";
    Place endAddress = null;
    GoogleMap appMap;
    Driver confirmedDriver;
    PlacesClient placesClient;
    LocationRequest locationRequest;
    private Marker marker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private Location currentLocation = null;
    private int index, next;
    private Polyline blackPolyline, greyPolyLine;
    private MutableLiveData<Boolean> driverReached;
    LatLng startPosition, endPosition;
    boolean lastTrip = true;
    boolean tripStarted=false;
    ValueAnimator polylineAnimator;
    Runnable animationTask;

    public HomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext());
        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(), KEY);
            placesClient = Places.createClient(this.requireContext());
        }
        if (checkPermission()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                if (location != null) {
                    currentLocation = location;
                }
            });
        }
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        driverReached = new MutableLiveData<>(false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mMapView = view.findViewById(R.id.mapView2);
        initGoogleMap(savedInstanceState);
        confirmButton = view.findViewById(R.id.floating_action_button);
        confirmedDriver = HomeFragmentArgs.fromBundle(getArguments()).getConfirmDriver();
        ExtendedFloatingActionButton cancel_ride = view.findViewById(R.id.cancel_ride);
        driverReached.observe(getViewLifecycleOwner(), r -> {
            if (r && lastTrip) {
                lastTrip = false;
                cancel_ride.setVisibility(View.GONE);
                endAddress = HomeFragmentArgs.fromBundle(getArguments()).getDriverDest();
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), location -> {
                            if (location != null) {
                                LatLng start = new LatLng(location.getLatitude(), location.getLongitude());
                                appMap.clear();
                                appMap.addMarker(new MarkerOptions().position(driverDestination));
                                appMap.addMarker(new MarkerOptions().position(start));
                                tripStarted=true;
                                getDirections(start, Objects.requireNonNull(endAddress.getLatLng()), false);
                            }
                        });
            }
        });

        confirmButton.setOnClickListener(v -> {
            if (endAddress != null && checkPermission()) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                    if (location != null) {
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(HomeFragmentDirections.actionNavigationHomeToNavigationSelectRide(location, endAddress, endAddress.getLatLng()));
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
                            getDirections(new LatLng(location.getLatitude(), location.getLongitude()), endAddress.getLatLng(), false);
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

        if (confirmedDriver != null) {
            confirmButton.setVisibility(View.GONE);
            FloatingActionButton driver_info = view.findViewById(R.id.driver_info);
            cancel_ride.setOnClickListener(v -> {
                confirmButton.setVisibility(View.VISIBLE);
                cancel_ride.setVisibility(View.GONE);
                driver_info.setVisibility(View.GONE);
                lastTrip = true;
                confirmedDriver = null;
                handler.removeCallbacks(animationTask);
                endAddress = null;
                driverReached.setValue(false);
                polylineAnimator.end();
                appMap.clear();
            });
            driver_info.setVisibility(View.VISIBLE);
            cancel_ride.setVisibility(View.VISIBLE);
            LatLng start = new LatLng(confirmedDriver.getLatitude(), confirmedDriver.getLongitude());
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng end = new LatLng(location.getLatitude(), location.getLongitude());
                            getDirections(start, end, true);
                        }
                    });
        }

        return view;
    }

    private void updateMap() {
        if (endAddress != null) {
            if (checkPermission()) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                    if (location != null && HaversineDistance.distance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude())) >= 30) {
                        currentLocation = location;
                        appMap.clear();
                        appMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(endAddress.getLatLng())));
                        if(tripStarted){
                            appMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
                        }
                        if (HaversineDistance.distance(new LatLng(location.getLatitude(), location.getLongitude()), endAddress.getLatLng()) <= 30) {
                            Toast.makeText(this.requireContext(), "You are on destination", Toast.LENGTH_LONG).show();
                            tripStarted=false;
                            appMap.clear();
                        } else {
                            getDirections(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), endAddress.getLatLng(), false);
                        }
                    }
                });
            }
        }
    }

    public void getDirections(LatLng startLocation, LatLng endLocation, boolean playAnimation) {
        UnitApp app = ((UnitApp) requireActivity().getApplication());
        String origin = startLocation.latitude + "," + startLocation.longitude;
        String destination = endLocation.latitude + "," + endLocation.longitude;
        app.getMapsRepository().getDirections(origin, destination, KEY).observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == com.example.unitapp.repository.Status.SUCCESS) {
                DirectionResponse directionResponse = r.getData();
                Route mainRoute = Objects.requireNonNull(directionResponse).getRoutes().get(0);
                String polylinePoint = mainRoute.getOverviewPolyline().getPoints();
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(10);
                polylineOptions.addAll(PolyUtil.decode(polylinePoint));
                if (!playAnimation) appMap.addPolyline(polylineOptions);
                if (playAnimation)
                    drawPolyLineAndAnimateCar(PolyUtil.decode(polylinePoint), startLocation);
            }
        });
    }

    private void drawPolyLineAndAnimateCar(List<LatLng> polyLineList, LatLng currentLocation) {
        //Adjusting bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        appMap.animateCamera(mCameraUpdate);

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(5);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = appMap.addPolyline(polylineOptions);

        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(5);
        blackPolylineOptions.color(Color.BLACK);
        blackPolylineOptions.startCap(new SquareCap());
        blackPolylineOptions.endCap(new SquareCap());
        blackPolylineOptions.jointType(ROUND);
        blackPolyline = appMap.addPolyline(blackPolylineOptions);


        polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(2000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(valueAnimator -> {
            List<LatLng> points = greyPolyLine.getPoints();
            int percentValue = (int) valueAnimator.getAnimatedValue();
            int size = points.size();
            int newPoints = (int) (size * (percentValue / 100.0f));
            List<LatLng> p = points.subList(0, newPoints);
            blackPolyline.setPoints(p);
        });
        polylineAnimator.start();
        if (marker == null) marker = appMap.addMarker(new MarkerOptions().position(currentLocation)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
        handler = new Handler();
        index = -1;
        next = 1;
        animationTask = new Runnable() {
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }

            @Override
            public void run() {
                if (index < polyLineList.size() - 1) {
                    index++;
                    next = index + 1;
                }
                if (index < polyLineList.size() - 1) {
                    startPosition = polyLineList.get(index);
                    endPosition = polyLineList.get(next);
                }
                if (index == 0) {
                    BeginJourneyEvent beginJourneyEvent = new BeginJourneyEvent();
                    beginJourneyEvent.setBeginLatLng(startPosition);
                    JourneyEventBus.getInstance().setOnJourneyBegin(beginJourneyEvent);
                }
                if (index == polyLineList.size() - 1) {
                    EndJourneyEvent endJourneyEvent = new EndJourneyEvent();
                    endJourneyEvent.setEndJourneyLatLng(new LatLng(polyLineList.get(index).latitude,
                            polyLineList.get(index).longitude));
                    JourneyEventBus.getInstance().setOnJourneyEnd(endJourneyEvent);
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(3000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(valueAnimator1 -> {
                    v = valueAnimator1.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v)
                            * startPosition.longitude;
                    lat = v * endPosition.latitude + (1 - v)
                            * startPosition.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    CurrentJourneyEvent currentJourneyEvent = new CurrentJourneyEvent();
                    currentJourneyEvent.setCurrentLatLng(newPos);
                    JourneyEventBus.getInstance().setOnJourneyUpdate(currentJourneyEvent);
                    marker.setPosition(newPos);
                    marker.setAnchor(0.5f, 0.5f);
                    marker.setRotation(getBearing(startPosition, newPos));
                    appMap.animateCamera(CameraUpdateFactory.newCameraPosition
                            (new CameraPosition.Builder().target(newPos)
                                    .zoom(15.5f).build()));
                });
                valueAnimator.start();
                if (index != polyLineList.size() - 1) {
                    handler.postDelayed(this, 3000);
                } else {
                    greyPolyLine.remove();
                    blackPolyline.remove();
                    driverReached.postValue(true);
                }

            }
        };
        handler.postDelayed(animationTask, 3000);

    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        double v = Math.toDegrees(Math.atan(lng / lat));
        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) v;
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - v) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (v + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - v) + 270);
        return -1;
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