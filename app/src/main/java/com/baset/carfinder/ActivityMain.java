package com.baset.carfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class ActivityMain extends AppCompatActivity implements View.OnClickListener, Constants {
    private Toolbar toolbar;
    private FloatingActionButton actionButton;
    private SqliteHelper sqliteHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setupViews();
        setupToolbar();
    }

    private void init() {
        sqliteHelper = new SqliteHelper(this);
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        actionButton = findViewById(R.id.float_addNewParking);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.float_addNewParking:
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    startActivityForResult(intent, LAT_LNG_INTENT_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case LAT_LNG_INTENT_REQUEST_CODE:
                if (resultCode==RESULT_OK){
                    double latitude=data.getDoubleExtra(KEY_LATITUDE_RESULT,0);
                    double longitude=data.getDoubleExtra(KEY_LONGITUDE_RESULT,0);
                    String address = data.getStringExtra(KEY_CAR_PARK_ADDRESS_RESULT);
                    Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
                }
        }
    }
}
