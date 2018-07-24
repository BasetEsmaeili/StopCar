package com.baset.carfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class AdapterCustomMap implements GoogleMap.InfoWindowAdapter,Constants {
    private View view;
    private Context context;
    private TextView textView;
    public AdapterCustomMap(Context context){
        this.context=context;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        view= LayoutInflater.from(context).inflate(R.layout.itemview_custom_info,null);
        textView=view.findViewById(R.id.marker_info_window_text);
        if (marker.getTag()==CAR_LOCATION_TAG){
            textView.setText(context.getResources().getString(R.string.car_location));
        }else if (marker.getTag()==USER_LOCATION_TAG){
            textView.setText(context.getResources().getString(R.string.user_location));
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
