package com.baset.carfinder.activity;

import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baset.carfinder.BuildConfig;
import com.baset.carfinder.R;
import com.baset.carfinder.constants.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wiadevelopers.com.directionlib.DirectionCallback;
import wiadevelopers.com.directionlib.GoogleDirection;
import wiadevelopers.com.directionlib.constant.TransportMode;
import wiadevelopers.com.directionlib.constant.Unit;
import wiadevelopers.com.directionlib.model.Direction;
import wiadevelopers.com.directionlib.model.RouteInfo;
import wiadevelopers.com.directionlib.util.MapUtils;

public class ActivityDirections extends AppCompatActivity implements OnMapReadyCallback, Constants, View.OnClickListener {
    private static final String TAG = "Direction Log:";
    private static int COLOR_WHITE;
    private static int COLOR_PRIMARY;
    private static final int NONE = 65;
    private static final int WALKING = 73;
    private static final int DRIVING = 95;
    private GoogleMap mMap;
    private double intent_latitude;
    private double intent_longitude;
    private String date;
    private String clock;
    private String destenatioonAddress;
    private Toolbar toolbar;
    private Marker userLocation;
    private Marker carParkedLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FloatingActionButton btn_requestDirection;
    private TextView txtOrigin;
    private TextView txtDestination;
    private RelativeLayout rltvDriving;
    private RelativeLayout rltvWalking;
    private ImageView imgDriving;
    private ImageView imgWalking;
    private TextView txtDriving;
    private TextView txtWalking;
    private ArrayList<RouteInfo> routeInfosDriving = new ArrayList<>();
    private ArrayList<Polyline> polylinesDriving = new ArrayList<>();
    private ArrayList<RouteInfo> routeInfosWalking = new ArrayList<>();
    private ArrayList<Polyline> polylinesWalking = new ArrayList<>();
    private ProgressBar progressBar;
    private int index = -1;
    private String transportmode=TransportMode.DRIVING;
    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null) {
            getLastLocation();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        getIntents();
        setupViews();
        setupToolbar();
        setupDirection();
        setupMap();
        activator(NONE);
    }

    private void setupDirection() {
        txtDriving.setText("-");
        txtWalking.setText("-");
        txtDestination.setText(destenatioonAddress);
        COLOR_WHITE = ContextCompat.getColor(getBaseContext(), R.color.white);
        COLOR_PRIMARY = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
        btn_requestDirection.setVisibility(View.GONE);
    }


    private void drawRoutes(int num) {
        for (int i = 0; i < polylinesDriving.size(); i++)
            polylinesDriving.get(i).remove();

        for (int i = 0; i < polylinesWalking.size(); i++)
            polylinesWalking.get(i).remove();


        polylinesDriving.clear();
        polylinesWalking.clear();
        switch (num) {
            case DRIVING:
                for (int i = 0; i < routeInfosDriving.size(); i++) {
                    Polyline polyline = mMap.addPolyline(routeInfosDriving.get(i).getPolylineOptions());
                    polyline.setTag(i);
                    polylinesDriving.add(polyline);

                }

                break;
            case WALKING:
                for (int i = 0; i < routeInfosWalking.size(); i++) {
                    Polyline polyline = mMap.addPolyline(routeInfosWalking.get(i).getPolylineOptions());
                    polyline.setPattern(MapUtils.getPattern(MapUtils.patternType.DOT));
                    polyline.setTag(i);
                    polylinesWalking.add(polyline);

                }

                break;
        }
    }

    private void setListeners() {
        if (userLocation != null && carParkedLocation != null) {
            btn_requestDirection.setVisibility(View.VISIBLE);
        }
        rltvDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (routeInfosDriving.size() != 0) {
                    activator(DRIVING);
                }
            }
        });
        rltvWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (routeInfosWalking.size() != 0) {
                    activator(WALKING);
                }
            }
        });
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                String tag = polyline.getTag().toString();
                index = Integer.parseInt(tag);
                if (transportmode.equals(TransportMode.DRIVING)){
                    polylinesDriving.get(index).remove();
                    final Polyline mPolyline = mMap.addPolyline(routeInfosDriving.get(index).getPolylineOptions());
                    mPolyline.setTag(index);
                    polylinesDriving.set(index, mPolyline);
                    for (int i = 0; i < polylinesDriving.size(); i++) {
                        if (i != index) {
                            polylinesDriving.get(i).setColor(Direction.UNSELECTED_ROUTE);
                        } else {
                            polylinesDriving.get(i).setColor(Direction.SELECTED_ROUTE);
                        }

                    }
                    txtDriving.setText(routeInfosDriving.get(index).getDurationText());
                }else if (transportmode.equals(TransportMode.WALKING)){
                    polylinesWalking.get(index).remove();
                    final Polyline mPolyline = mMap.addPolyline(routeInfosWalking.get(index).getPolylineOptions());
                    mPolyline.setTag(index);
                    mPolyline.setPattern(MapUtils.getPattern(MapUtils.patternType.DOT));
                    polylinesWalking.set(index, mPolyline);
                    for (int i = 0; i < polylinesWalking.size(); i++) {
                        if (i != index) {
                            polylinesWalking.get(i).setColor(Direction.UNSELECTED_ROUTE);
                        } else {
                            polylinesWalking.get(i).setColor(Direction.SELECTED_ROUTE);
                        }

                    }
                    txtWalking.setText(routeInfosWalking.get(index).getDurationText());
                }

            }
        });
    }

    private void drivengRequest() {
        // TODO: 8/15/2018 Change afther release
        progressBar.setVisibility(View.VISIBLE);
        GoogleDirection.withServerKey(BuildConfig.USER_LOCATION_DEBUG)
                .from(userLocation.getPosition())
                .to(carParkedLocation.getPosition())
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(true)
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String s) {
                        progressBar.setVisibility(View.GONE);
                        if (direction.isOK()) {
                            routeInfosDriving = direction.getRouteInfo(ActivityDirections.this, 5);
                            activator(DRIVING);
                            txtDriving.setText(routeInfosDriving.get(routeInfosDriving.size() - 1).getDurationText());

                        } else {
                            Log.e(TAG, direction.getErrorMessage());
                            showMessage(getResources().getString(R.string.error_direction_request), SNACKBAR, SHORT);
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable throwable) {

                    }
                });
    }

    private void walkingRequest() {
        // TODO: 8/15/2018 Change afther release
        progressBar.setVisibility(View.VISIBLE);
        GoogleDirection.withServerKey(BuildConfig.USER_LOCATION_DEBUG)
                .from(userLocation.getPosition())
                .to(carParkedLocation.getPosition())
                .transportMode(TransportMode.WALKING)
                .alternativeRoute(true)
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String s) {
                        progressBar.setVisibility(View.GONE);
                        if (direction.isOK()) {
                            routeInfosWalking = direction.getRouteInfo(ActivityDirections.this, 5);
                            activator(WALKING);
                            txtWalking.setText(routeInfosWalking.get(routeInfosWalking.size() - 1).getDurationText());
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable throwable) {

                    }
                });
    }

    private void activator(int num) {
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        switch (num) {
            case NONE:
                rltvWalking.setBackgroundResource(R.drawable.button_blue);
                txtWalking.setTextColor(COLOR_WHITE);
                imgWalking.setColorFilter(COLOR_WHITE, mode);

                rltvDriving.setBackgroundResource(R.drawable.button_blue);
                txtDriving.setTextColor(COLOR_WHITE);
                imgDriving.setColorFilter(COLOR_WHITE, mode);
                break;
            case DRIVING:
                rltvWalking.setBackgroundResource(R.drawable.button_blue);
                txtWalking.setTextColor(COLOR_WHITE);
                imgWalking.setColorFilter(COLOR_WHITE, mode);

                rltvDriving.setBackgroundResource(R.drawable.button_white);
                txtDriving.setTextColor(COLOR_PRIMARY);
                imgDriving.setColorFilter(COLOR_PRIMARY, mode);
                drawRoutes(DRIVING);
                transportmode=TransportMode.DRIVING;
                break;
            case WALKING:
                rltvWalking.setBackgroundResource(R.drawable.button_white);
                txtWalking.setTextColor(COLOR_PRIMARY);
                imgWalking.setColorFilter(COLOR_PRIMARY, mode);

                rltvDriving.setBackgroundResource(R.drawable.button_blue);
                txtDriving.setTextColor(COLOR_WHITE);
                imgDriving.setColorFilter(COLOR_WHITE, mode);
                drawRoutes(WALKING);
                transportmode=TransportMode.WALKING;
                break;
        }
    }

    private void setupToolbar() {
        toolbar.setTitle(date + " " + clock);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        btn_requestDirection = findViewById(R.id.btnRequestDirection);
        txtDestination = findViewById(R.id.txtDestination);
        txtOrigin = findViewById(R.id.txtOrigin);
        txtWalking = findViewById(R.id.txtWalking);
        txtDriving = findViewById(R.id.txtDriving);
        imgDriving = findViewById(R.id.imgDriving);
        imgWalking = findViewById(R.id.imgWalking);
        rltvDriving = findViewById(R.id.rltvDriving);
        rltvWalking = findViewById(R.id.rltvWalking);
        progressBar = findViewById(R.id.pgb_diraction);
    }

    private void getIntents() {
        intent_latitude = getIntent().getDoubleExtra(KEY_DIRECTION_LATITUDE, 0);
        intent_longitude = getIntent().getDoubleExtra(KEY_DIRECTION_LONGITUDE, 0);
        date = getIntent().getStringExtra(KEY_DIRECTION_DATE);
        clock = getIntent().getStringExtra(KEY_DIRECTION_CLOCK);
        destenatioonAddress = getIntent().getStringExtra(KEY_DIRECTION_ADDRESS);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addcarParkLocation();
        setupLocationRequest();
        startLocationUpdates();
    }

    private void setupLocationRequest() {
        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM_DIRECTION);
                        mMap.animateCamera(cameraUpdate);
                        userLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(
                                getResources().getString(R.string.user_location)
                        ));
                        setListeners();
                        setupParkedPlaceAddress(location.getLatitude(), location.getLongitude());
                    } else {
                        showMessage(getResources().getString(R.string.error_find_location), SNACKBAR, SHORT);
                    }
                }
            });
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(500);
            locationRequest.setFastestInterval(500);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        if (userLocation != null) {
                            userLocation.remove();
                        }
                        userLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()
                        )).title(getResources().getString(R.string.user_location)));
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM_DIRECTION);
                        mMap.animateCamera(cameraUpdate);
                        setListeners();
                        setupParkedPlaceAddress(location.getLatitude(), location.getLongitude());
                    }
                }
            };
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addcarParkLocation() {

        carParkedLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(intent_latitude, intent_longitude)).title(getResources().getString(R.string.car_location)).icon(BitmapDescriptorFactory.fromResource(
                R.drawable.car_marker
        )));
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    public void showMessage(String message, String type, String duration) {
        switch (type) {
            case TOAST:
                if (duration.equals(SHORT))
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                else if (duration.equals(LONG))
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                break;
            case SNACKBAR:
                switch (duration) {
                    case SHORT:
                        Snackbar.make(findViewById(R.id.direction_root), message, Snackbar.LENGTH_SHORT).show();
                        break;
                    case LONG:
                        Snackbar.make(findViewById(R.id.direction_root), message, Snackbar.LENGTH_LONG).show();
                        break;
                    case INDEFINITE:
                        Snackbar.make(findViewById(R.id.direction_root), message, Snackbar.LENGTH_INDEFINITE).show();
                        break;
                }
        }
    }

    private void startLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    private void getLastLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(ActivityDirections.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();
                            if (userLocation != null) {
                                userLocation.remove();
                            }
                            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                    latLng, DEFAULT_MAP_ZOOM_DIRECTION);
                            mMap.animateCamera(cameraUpdate);
                            userLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title(getResources()
                                    .getString(R.string.user_location)));
                            setListeners();
                            setupParkedPlaceAddress(lastLocation.getLatitude(), lastLocation.getLongitude());
                        } else {
                            showMessage(getResources().getString(R.string.error_find_location), SNACKBAR, SHORT);
                        }
                    }
                });
    }

    private void setupParkedPlaceAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String location = addresses.get(0).getThoroughfare();

            if (location == null || location.equals(""))
                location = addresses.get(0).getSubThoroughfare();
            if (location == null || location.equals(""))
                location = addresses.get(0).getLatitude() + " , " + addresses.get(0).getLongitude();
            txtOrigin.setText(location);
        } catch (IOException e
                ) {
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_myLocationDirection:
                getLastLocation();
                break;
            case R.id.btnRequestDirection:
                drivengRequest();
                walkingRequest();
                break;
        }
    }
}
