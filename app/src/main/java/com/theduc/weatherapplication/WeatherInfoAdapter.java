package com.theduc.weatherapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WeatherInfoAdapter extends RecyclerView.Adapter<WeatherInfoAdapter.ViewHolder> {

    private List<WeatherInfo> weatherInfoList;
    private Context context;

    public WeatherInfoAdapter( Context context, List<WeatherInfo> weatherInfoList) {
        this.weatherInfoList = weatherInfoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherInfo weatherInfo = weatherInfoList.get(position);
        holder.titleTextView.setText(weatherInfo.getTitle());
        holder.valueTextView.setText(weatherInfo.getValue());
    }

    @Override
    public int getItemCount() {
        return weatherInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView valueTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
        }
    }
}

