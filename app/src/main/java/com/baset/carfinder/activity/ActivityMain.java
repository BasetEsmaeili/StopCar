package com.baset.carfinder.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.baset.carfinder.constants.Constants;
import com.baset.carfinder.R;
import com.baset.carfinder.database.SqliteHelper;

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
                    String carName = data.getStringExtra(KEY_CAR_NAME_RESULT);
                    String carColor = data.getStringExtra(KEY_CAR_Color_RESULT);
                    String carPlaque = data.getStringExtra(KEY_CAR_PLAQUE_RESULT);
                    String parkDate = data.getStringExtra(KEY_CAR_DATE_PARK_RESULT);
                    String parkClock = data.getStringExtra(KEY_CAR_CLOCK_PARK_RESULT);
                    String parkAddress = data.getStringExtra(KEY_CAR_PARK_ADDRESS_RESULT);
                    double latitude = data.getDoubleExtra(KEY_LATITUDE_RESULT, 0);
                    double longitude = data.getDoubleExtra(KEY_LONGITUDE_RESULT, 0);
                    sqliteHelper.insertHistory(carName, carColor, carPlaque, parkDate, parkClock, parkAddress, latitude, longitude);

                }
        }
    }
}
