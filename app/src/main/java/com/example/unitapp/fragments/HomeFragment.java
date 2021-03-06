package com.example.unitapp.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unitapp.R;
import com.example.unitapp.UnitApp;
import com.example.unitapp.api.model.DirectionResponse;
import com.example.unitapp.api.model.Driver;
import com.example.unitapp.api.model.Route;
import com.example.unitapp.databinding.FragmentHomeBinding;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.maps.android.PolyUtil;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    boolean tripStarted = false;
    ValueAnimator polylineAnimator;
    private final int PADDING = 220;
    Runnable animationTask;
    private MarkerOptions markerOptions;
    private Marker tripLocation;
    FragmentHomeBinding binding;

    public HomeFragment() {

    }


    @SuppressLint("MissingPermission")
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

    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        mMapView = binding.mapView2;
        initGoogleMap(savedInstanceState);
        confirmButton = binding.floatingActionButton;
        confirmedDriver = HomeFragmentArgs.fromBundle(getArguments()).getConfirmDriver();
        MaterialCardView driverInfoCard = binding.driverInfoCard;
        driverReached.observe(getViewLifecycleOwner(), r -> {
            if (r && lastTrip) {
                lastTrip = false;
                binding.timeEstimate.setText(R.string.arrival_time);
                LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of((int)confirmedDriver.getEstimatedArrival(), ChronoUnit.MINUTES));
                @SuppressLint("DefaultLocale") String arrival_time = String.format("%02d:%02d",dateTime.getHour(), dateTime.getMinute());
                binding.timeEstimateValue.setText(arrival_time);
                endAddress = HomeFragmentArgs.fromBundle(getArguments()).getDriverDest();
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), location -> {
                            if (location != null) {
                                LatLng start = new LatLng(location.getLatitude(), location.getLongitude());
                                appMap.clear();
                                appMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(endAddress.getLatLng())));
                                markerOptions = new MarkerOptions();
                                markerOptions.position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car));
                                markerOptions.rotation(location.getBearing()).anchor((float) 0.5, (float) 0.5);
                                tripLocation = appMap.addMarker(markerOptions);
                                getDirections(start, endAddress.getLatLng(), false);
                                CameraUpdate cameraUpdate = modifyMapZoom(location, endAddress);
                                appMap.animateCamera(cameraUpdate);
                                tripStarted = true;
                                appMap.setMyLocationEnabled(false);
                            }
                        });
            }
        });


        binding.cancelRideBtn.setOnClickListener(cr -> {
            Dialog cancelDialog = new Dialog(requireActivity());
            cancelDialog.setContentView(R.layout.cancel_ride_dialog);
            cancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cancelDialog.show();
            MaterialButton acceptButton = cancelDialog.findViewById(R.id.accept_btn);
            MaterialButton cancelButton = cancelDialog.findViewById(R.id.cancel_btn);
            acceptButton.setOnClickListener(acceptView -> {
                confirmButton.setVisibility(View.VISIBLE);
                binding.driverInfoCard.setVisibility(View.GONE);
                lastTrip = true;
                confirmedDriver = null;
                handler.removeCallbacks(animationTask);
                endAddress = null;
                driverReached.setValue(false);
                polylineAnimator.end();
                appMap.clear();
                cancelDialog.dismiss();
            });
            cancelButton.setOnClickListener(cancelView -> cancelDialog.dismiss());
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
                Toast.makeText(this.requireContext(), R.string.enter_destination, Toast.LENGTH_SHORT).show();
            }
        });
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteFragment.setCountries("AR");
        autocompleteFragment.setHint(getString(R.string.where_are_you_going));

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
                            CameraUpdate cameraUpdate = modifyMapZoom(location, endAddress);
                            appMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(endAddress.getLatLng())));
                            getDirections(new LatLng(location.getLatitude(), location.getLongitude()), endAddress.getLatLng(), false);
                            appMap.animateCamera(cameraUpdate);
                        }
                    });
                }

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, getString(R.string.error_ocurred) + status);
            }
        });

        if (confirmedDriver != null) {
            driverInfoCard.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
            binding.platePlaceholder.setText(confirmedDriver.getPlate());
            binding.driverNameTextview.setText(confirmedDriver.getName());
            String fullDrive = confirmedDriver.getBrand() + " " + confirmedDriver.getCarModel();
            binding.modelBrandPlaceholder.setText(fullDrive);
            LatLng start = new LatLng(confirmedDriver.getLatitude(), confirmedDriver.getLongitude());
            LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of((int)confirmedDriver.getEstimatedPickup() / 10, ChronoUnit.MINUTES));
            @SuppressLint("DefaultLocale") String pickup_time = String.format("%02d:%02d",dateTime.getHour(), dateTime.getMinute());
            binding.timeEstimateValue.setText(pickup_time);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng end = new LatLng(location.getLatitude(), location.getLongitude());
                            getDirections(start, end, true);
                        }
                    });
        }

        return binding.getRoot();
    }

    private CameraUpdate modifyMapZoom(Location startlocation, Place destination) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(Objects.requireNonNull(destination.getLatLng()));
        builder.include(new LatLng(startlocation.getLatitude(), startlocation.getLongitude()));
        LatLngBounds bounds = builder.build();
        return CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
    }

    @SuppressLint("MissingPermission")
    private void updateMap() {
        if (endAddress != null) {
            if (checkPermission()) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                    if (location != null) {
                        if (tripStarted) {
                            tripLocation.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                            markerOptions.rotation(location.getBearing()).anchor((float) 0.5, (float) 0.5);
                        }
                        if (HaversineDistance.distance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude())) >= 30) {
                            currentLocation = location;
                            appMap.clear();
                            appMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(endAddress.getLatLng())));
                            if (tripStarted) {
                                markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car));
                                markerOptions.rotation(location.getBearing()).anchor((float) 0.5, (float) 0.5);
                                tripLocation = appMap.addMarker(markerOptions);
                            }
                            if (HaversineDistance.distance(new LatLng(location.getLatitude(), location.getLongitude()), endAddress.getLatLng()) <= 30) {
                                Dialog finishTrip = new Dialog(requireActivity());
                                finishTrip.setContentView(R.layout.destination_reached_dialog);
                                finishTrip.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                finishTrip.show();
                                finishTrip.findViewById(R.id.done_button).setOnClickListener(c -> finishTrip.dismiss());
                                confirmButton.setVisibility(View.VISIBLE);
                                binding.driverInfoCard.setVisibility(View.GONE);
                                lastTrip = true;
                                confirmedDriver = null;
                                handler.removeCallbacks(animationTask);
                                endAddress = null;
                                driverReached.setValue(false);
                                polylineAnimator.end();
                                appMap.clear();
                                if (checkPermission())
                                    appMap.setMyLocationEnabled(true);
                            } else {
                                getDirections(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), endAddress.getLatLng(), false);
                            }
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
                polylineOptions.color(Color.WHITE);
                polylineOptions.width(7);
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
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
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
        blackPolylineOptions.color(Color.WHITE);
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NotNull GoogleMap map) {
        appMap = map;
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_map);
        appMap.setMapStyle(mapStyleOptions);
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

    @SuppressLint("MissingPermission")
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