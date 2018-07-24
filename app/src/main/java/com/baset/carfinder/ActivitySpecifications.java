package com.baset.carfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.Arrays;

public class ActivitySpecifications extends AppCompatActivity implements Constants {
    private String[] colors;
    private TextView carModel;
    private Context context;
    private Typeface typeface;
    private Spinner carColor;
    private ArrayList<String> colors_array = new ArrayList<String>();
    private String singleColor;
    private Toolbar toolbar;
    private EditText carName;
    private String str_carName;
    private ImageView img_save;
    private EditText irCode;
    private EditText plaque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specifications);
        context = ActivitySpecifications.this;
        typeface = Typeface.createFromAsset(getAssets(), "font_sans.ttf");
        setupViews();
        setupToolbar();
        setupDialogs();
    }

    private void setupPreference() {
        if (carName.length() > 0 && carModel.getText().toString() != null && singleColor != null && irCode.length() > 0 && plaque.length() > 0){
            SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_PREFERENCE_CAR_NAME, carName.getText().toString());
            editor.putString(KEY_PREFERENCE_MODEL, carModel.getText().toString());
            editor.putString(KEY_PREFERENCE_COLOR, singleColor);
            editor.putString(KEY_PREFERENCE_IR_CODE,irCode.getText().toString());
            editor.putString(KEY_PREFERENCE_PLAQUE,plaque.getText().toString());
            editor.apply();
            startActivity(new Intent(getBaseContext(),ActivityMain.class));
        }else {
            Snackbar.make(findViewById(R.id.specifications_root),getResources().getString(R.string.all_places_be_completed),Snackbar.LENGTH_SHORT).show();
        }


    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

    }

    private void setupDialogs() {
        colors = getResources().getStringArray(R.array.colors);
        carModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_car_model, null);
                final NumberPicker carColorPicker = view.findViewById(R.id.carModel_NumberPicker);
                carColorPicker.setMinValue(1370);
                carColorPicker.setMaxValue(1400);
                carColorPicker.setValue(1397);
                carColorPicker.setTypeface(typeface);
                builder.setView(view);
                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        carModel.setText("" + carColorPicker.getValue());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(true);
                AlertDialog alert11 = builder.create();
                alert11.show();
            }
        });
        ArrayAdapter<String> sp_adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, colors);
        carColor.setAdapter(sp_adapter);
        colors_array.addAll(Arrays.asList(colors));
        carColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                singleColor = colors_array.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupPreference();
            }
        });
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        carModel = findViewById(R.id.tv_carModel);
        carColor = findViewById(R.id.sp_carColor);
        carName = findViewById(R.id.edt_carName);
        img_save = findViewById(R.id.img_save);
        irCode=findViewById(R.id.edt_irCode);
        plaque=findViewById(R.id.edt_plaque);
    }

}
