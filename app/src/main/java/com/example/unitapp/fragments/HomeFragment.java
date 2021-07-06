package com.example.unitapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitapp.R;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.PolyUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import static com.example.unitapp.utils.Constants.MAPVIEW_BUNDLE_KEY;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    FusedLocationProviderClient fusedLocationClient;
    Button confirmButton;
    private static final String TAG = "info:";
    Place endAddress = null;
    GoogleMap appMap;
    PlacesClient placesClient;
    LocationRequest locationRequest;


    private void updateMap() {
        if (endAddress != null) {
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
        }
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
        locationRequest.setInterval(1000*90);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mMapView = view.findViewById(R.id.mapView2);
        initGoogleMap(savedInstanceState);
        confirmButton = view.findViewById(R.id.button3);
        confirmButton.setOnClickListener(v -> {
            if (endAddress != null && checkPermission()) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                    if (location != null) {
                        ChooseRideFragment chooseRideFragment = new ChooseRideFragment(location, endAddress);
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
                appMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(endAddress.getLatLng())));
                getDirections();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(endAddress.getLatLng(), 15);
                appMap.animateCamera(cameraUpdate);
            }


            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        return view;
    }

    public void getDirections() {
        if(checkPermission()){
            fusedLocationClient.getLastLocation().addOnSuccessListener(this.requireActivity(), location -> {
                if (location != null) {
                    StringBuilder sb;
                    sb = new StringBuilder();
                    sb.append("https://maps.googleapis.com/maps/api/directions/json?");
                    sb.append("origin=" + location.getLatitude() + "," + location.getLongitude());
                    sb.append("&destination=" + Objects.requireNonNull(endAddress.getLatLng()).latitude + "," + endAddress.getLatLng().longitude);
                    sb.append("&key=" + "AIzaSyAMElDromNlk946AR6VTHSpkOvaV84Kk2Y");

                    GetDirectionsData getDirectionsData = new GetDirectionsData();
                    getDirectionsData.execute(sb.toString());
                }
            });
        }
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        URL url = null;
        try {
            url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //get response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    private class GetDirectionsData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps");
                int count = jsonArray.length();
                String[] polyline_array = new String[count];

                JSONObject jsonObject2;

                for (int i = 0; i < count; i++) {
                    jsonObject2 = jsonArray.getJSONObject(i);
                    String polygone = jsonObject2.getJSONObject("polyline").getString("points");
                    polyline_array[i] = polygone;
                }

                int count2 = polyline_array.length;

                for (int i = 0; i < count2; i++) {
                    PolylineOptions options2 = new PolylineOptions();
                    options2.color(Color.BLUE);
                    options2.width(10);
                    options2.addAll(PolyUtil.decode(polyline_array[i]));
                    appMap.addPolyline(options2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
        if(checkPermission()){
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
    private boolean checkPermission(){
        return ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void startLocationUpdates() {
        if(checkPermission()){
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }
    private void stopLocationUpdates(){
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