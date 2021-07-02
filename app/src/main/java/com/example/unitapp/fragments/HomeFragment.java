package com.example.unitapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.unitapp.utils.Constants.MAPVIEW_BUNDLE_KEY;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    FusedLocationProviderClient fusedLocationClient;
    Button confirmButton;
    Address endAddress;
    GoogleMap appMap;
    EditText startLocation, endLocation;
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        startLocation = view.findViewById(R.id.fromDirection);
        endLocation = view.findViewById(R.id.toDirection);
        startLocation.setText("current location", TextView.BufferType.NORMAL);
        startLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                }
            }
        });
        mMapView = view.findViewById(R.id.mapView2);
        confirmButton = view.findViewById(R.id.button3);
        confirmButton.setOnClickListener(v -> {
            ChooseRideFragment chooseRideFragment = new ChooseRideFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
            transaction.replace(R.id.container, chooseRideFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        initGoogleMap(savedInstanceState);
        return view;
    }
    private Runnable input_finish_checker = () -> {
        if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
            markLocation(startLocation.getText().toString());
            if(startLocation.getText().toString().compareTo("current location")!=0){
                markLocation(startLocation.getText().toString());
            }
        }
    };
    private void markLocation(String direction){
        Geocoder geocoder = new Geocoder(this.requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName("La Plata", 1);
            endAddress = addresses.get(0);
            appMap.addMarker(new MarkerOptions().position(new LatLng(endAddress.getLatitude(),endAddress.getLongitude())));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(endAddress.getLatitude(),endAddress.getLongitude()), 10);
            appMap.animateCamera(cameraUpdate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*public void requestDirections() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
            if (location != null) {
                StringBuilder sb;
                Object[] dataTransfer=new Object[4];
                sb=new StringBuilder();
                sb.append("https://maps.googleapis.com/maps/api/directions/json?");
                sb.append("origin="+location.getLatitude()+","+location.getLongitude());
                sb.append("&destination="+endAddress.getLatitude()+","+endAddress.getLongitude());
                sb.append("&key="+"AIzaSyAMElDromNlk946AR6VTHSpkOvaV84Kk2Y");
                sb.append("&alternatives=true");

                MultipleDirections directions=
                dataTransfer[0]=appMap;
                dataTransfer[1]=sb.toString();
                dataTransfer[2]=new LatLng(location.getLatitude(),location.getLongitude());
                dataTransfer[3]=new LatLng(endAddress.getLatitude(),endAddress.getLongitude());
            }
        });
    }*/
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
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(@NotNull GoogleMap map) {
        appMap=map;
        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
            if(location!=null){
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 17);
                map.animateCamera(cameraUpdate);
            }
        });
        map.setMyLocationEnabled(true);
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