package com.baset.carfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, Constants, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private ImageView doneBtn;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView img_state_changer;
    private String carName;
    private String carColor;
    private String irCode;
    private String carPlaque;
    private TextView cName;
    private TextView cColor;
    private TextView cPlaque;
    private TextView pDate;
    private TextView pClock;
    private TextView pLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Marker carMarker;
    @Override
    protected void onStart() {
        startLocationUpdates();
        super.onStart();
    }
    @Override
    protected void onResume() {
        getLastLocation();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setupViews();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.my_map);
        mapFragment.getMapAsync(this);
        setupLocationRequest();
        setupBottomSheet();
        setupPreferences();
        setupDialogContent();
    }

    private void setupDialogContent() {
        cName.setText(carName);
        cColor.setText(carColor);
        cPlaque.setText(getResources().getString(R.string.iran) + irCode + " " + carPlaque);
        PersianDate persianDate = new PersianDate();
        PersianDateFormat dateFormat = new PersianDateFormat("Y/m/d");
        String date = dateFormat.format(persianDate);
        PersianDateFormat dayNameFormat = new PersianDateFormat("l");
        String dayName = dayNameFormat.format(persianDate);
        pDate.setText(dayName + " " + date);
        PersianDateFormat clockFormat = new PersianDateFormat("H:i");
        String clock = clockFormat.format(persianDate);
        pClock.setText(clock);
    }

    private void setupPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        carName = sharedPreferences.getString(KEY_PREFERENCE_CAR_NAME, "نام خودرو");
        carColor = sharedPreferences.getString(KEY_PREFERENCE_COLOR, "رنگ خودرو");
        irCode = sharedPreferences.getString(KEY_PREFERENCE_IR_CODE, "ایران");
        carPlaque = sharedPreferences.getString(KEY_PREFERENCE_PLAQUE, "پلاک خودرو");
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        img_state_changer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override

            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    img_state_changer.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_keyboard_arrow_up_black_24dp));
                    img_state_changer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                } else {
                    img_state_changer.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_keyboard_arrow_down_black_24dp));
                    img_state_changer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    });
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        doneBtn = findViewById(R.id.img_doneBtn);
        img_state_changer = findViewById(R.id.dialog_placeDetail_changeState);
        cName = findViewById(R.id.dialog_placeDetail_cName);
        cColor = findViewById(R.id.dialog_placeDetail_cColor);
        cPlaque = findViewById(R.id.dialog_placeDetail_cPlaque);
        pDate = findViewById(R.id.dialog_placeDetail_pDate);
        pClock = findViewById(R.id.dialog_placeDetail_pClock);
        pLocation = findViewById(R.id.dialog_placeDetail_pLocation);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setupLocationRequest();
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSIN_REQUEST_CODE);
            } else {
               setupLocationRequest();
               startLocationUpdates();
            }

        } else {
            setupLocationRequest();
            startLocationUpdates();
        }
    }

    private void setupParkedPlaceAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
            String location = addresses.get(0).getThoroughfare();

            if (location == null || location.equals(""))
                location = addresses.get(0).getSubThoroughfare();
            if (location == null || location.equals(""))
                location = addresses.get(0).getLatitude() + " , " + addresses.get(0).getLongitude();

            pLocation.setText(location);
        } catch (IOException e
                ) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSIN_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupLocationRequest();
                    startLocationUpdates();
                } else {
                    showMessage(getResources().getString(R.string.error_find_location),TOAST,SHORT);
                }
                break;
        }
    }

    private void setupLocationRequest() {
        try {
            fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(getBaseContext());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
             if (location!=null){
                 carMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).snippet(
                         getResources().getString(R.string.car_location)
                 ));
                 carMarker.setTag(CAR_LOCATION_TAG);
                 setupParkedPlaceAddress(location.getLatitude(),location.getLongitude());
             }else {
                 showMessage(getResources().getString(R.string.error_find_location),TOAST,SHORT);
             }
                }
            });
            locationRequest=LocationRequest.create();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationCallback=new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location:locationResult.getLocations()){
                        if (carMarker!=null){
                            carMarker.remove();
                        }
                        carMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude()
                        )).snippet(getResources().getString(R.string.car_location)));
                        carMarker.setTag(CAR_LOCATION_TAG);
                        setupParkedPlaceAddress(location.getLatitude(),location.getLongitude());
                    }
                }
            };
        }catch (SecurityException ex){
            ex.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        stopLocationUpdates();
        super.onPause();

    }

    @Override
    protected void onStop() {
        stopLocationUpdates();
        super.onStop();
    }

    private void getLastLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(MapsActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();
                            if (carMarker!=null){
                                carMarker.remove();
                            }
                            carMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())).snippet(getResources()
                            .getString(R.string.car_location)));
                            carMarker.setTag(CAR_LOCATION_TAG);
                            setupParkedPlaceAddress(lastLocation.getLatitude(),lastLocation.getLongitude());
                        } else {
                            showMessage(getResources().getString(R.string.error_find_location),TOAST,SHORT);
                        }
                    }
                });
    }
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private  void showMessage(String message,String type,String duration){
        switch (type){
            case TOAST:
                if (duration.equals(SHORT))
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                else if (duration.equals(LONG))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            case SNACKBAR:
                if (duration.equals(SHORT))
                Snackbar.make(findViewById(R.id.map_root),message,Snackbar.LENGTH_SHORT).show();
                else if (duration.equals(LONG))
                    Snackbar.make(findViewById(R.id.map_root),message,Snackbar.LENGTH_LONG).show();
                else if (duration.equals(INDEFINITE))
                    Snackbar.make(findViewById(R.id.map_root),message,Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getLastLocation();
        return true;
    }
}
