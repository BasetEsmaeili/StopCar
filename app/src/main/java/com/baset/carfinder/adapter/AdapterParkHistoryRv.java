package com.baset.carfinder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baset.carfinder.Interface.HistoryItemClick;
import com.baset.carfinder.R;
import com.baset.carfinder.model.ModelParkHistory;

import java.util.List;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class AdapterParkHistoryRv extends RecyclerView.Adapter<AdapterParkHistoryRv.ViewHolderParkedPlacesRv> {
    private Context context;
    private List<ModelParkHistory> parkHistories;
    private int in_position;
    private HistoryItemClick itemClick;

    public AdapterParkHistoryRv(Context context, List<ModelParkHistory> parkHistories, HistoryItemClick itemClick) {
        this.context = context;
        this.parkHistories = parkHistories;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolderParkedPlacesRv onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemview_park_history, parent, false);
        return new ViewHolderParkedPlacesRv(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderParkedPlacesRv holder, int position) {
        in_position = position;
        holder.bindData(parkHistories.get(position));
    }

    @Override
    public int getItemCount() {
        return parkHistories.size();
    }

    public class ViewHolderParkedPlacesRv extends RecyclerView.ViewHolder {
        private TextView carName;
        private TextView carParkAddress;
        private TextView carParkDate;
        private TextView carParkClock;
        private TextView carPlaque;
        private TextView todayCheck;

        public ViewHolderParkedPlacesRv(View itemView) {
            super(itemView);
            carName = itemView.findViewById(R.id.itemview_tvCarNameandColor);
            carParkAddress = itemView.findViewById(R.id.itemview_tvParkAddress);
            carParkDate = itemView.findViewById(R.id.itemview_tvParkDate);
            carParkClock = itemView.findViewById(R.id.itemview_tvParkClock);
            carPlaque = itemView.findViewById(R.id.itemview_tvCarPlaque);
            todayCheck = itemView.findViewById(R.id.tv_today_check);
        }

        public void bindData(final ModelParkHistory parkHistory) {
            carName.setText(parkHistory.getCarName() + " " + parkHistory.getCarColor());
            carParkAddress.setText(parkHistory.getAddress());
            PersianDate persianDate = new PersianDate();
            PersianDateFormat dateFormat = new PersianDateFormat("l j F");
            String date = dateFormat.format(persianDate);
            carParkDate.setText(parkHistory.getDatePark());
            if (date.equals(parkHistory.getDatePark())) {
                todayCheck.setText(Html.fromHtml("&#8226;"));
            } else {
                todayCheck.setVisibility(View.GONE);
            }
            carParkClock.setText(parkHistory.getClockPark());
            carPlaque.setText(parkHistory.getCarPlaque());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.onHistoryItemClick(parkHistory.getLatitude(), parkHistory.getLongitude(), parkHistory.getDatePark(), parkHistory.getClockPark(), parkHistory.getAddress());
                }
            });
        }

    }

    public void addItem(ModelParkHistory modelParkHistory) {
        parkHistories.add(modelParkHistory);
        notifyItemInserted(in_position);
        notifyDataSetChanged();
    }

}
