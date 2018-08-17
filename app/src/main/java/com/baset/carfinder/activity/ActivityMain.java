package com.baset.carfinder.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.baset.carfinder.Interface.HistoryItemClick;
import com.baset.carfinder.adapter.AdapterParkHistoryRv;
import com.baset.carfinder.constants.Constants;
import com.baset.carfinder.R;
import com.baset.carfinder.database.SqliteHelper;
import com.baset.carfinder.model.ModelParkHistory;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity implements View.OnClickListener, Constants, HistoryItemClick {
    private Toolbar toolbar;
    private FloatingActionButton actionButton;
    private SqliteHelper sqliteHelper;
    private RecyclerView rv_history;
    private List<ModelParkHistory> parkHistories=new ArrayList<>();
    private AdapterParkHistoryRv adpaterRv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setupViews();
        setupToolbar();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,false);
        rv_history.setLayoutManager(manager);
        rv_history.setAdapter(adpaterRv);
       rv_history.addOnScrollListener(new RecyclerView.OnScrollListener() {
           @Override
           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               if (dy >0) {
                   // Scroll Down
                   if (actionButton.isShown()) {
                       actionButton.hide();
                   }
               }
               else if (dy <0) {
                   // Scroll Up
                   if (!actionButton.isShown()) {
                       actionButton.show();
                   }
               }
           }
       });
    }

    private void init() {
        sqliteHelper = new SqliteHelper(this);
        parkHistories.addAll(sqliteHelper.getAllHistory());
        adpaterRv=new AdapterParkHistoryRv(getBaseContext(),parkHistories,this);
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        actionButton = findViewById(R.id.float_addNewParking);
        rv_history=findViewById(R.id.parked_places_rv);
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
                    ModelParkHistory modelParkHistory=new ModelParkHistory();
                    modelParkHistory.setCarName(carName);
                    modelParkHistory.setCarColor(carColor);
                    modelParkHistory.setCarPlaque(carPlaque);
                    modelParkHistory.setDatePark(parkDate);
                    modelParkHistory.setClockPark(parkClock);
                    modelParkHistory.setAddress(parkAddress);
                    adpaterRv.addItem(modelParkHistory);
                }
        }
    }

    @Override
    public void onHistoryItemClick(double latitude, double longitude,String date,String clock,String address) {
        Intent intent=new Intent(getBaseContext(),ActivityDirections.class);
        intent.putExtra(KEY_DIRECTION_LATITUDE,latitude);
        intent.putExtra(KEY_DIRECTION_LONGITUDE,longitude);
        intent.putExtra(KEY_DIRECTION_DATE,date);
        intent.putExtra(KEY_DIRECTION_CLOCK,clock);
        intent.putExtra(KEY_DIRECTION_ADDRESS,address);
        startActivity(intent);
    }
}
