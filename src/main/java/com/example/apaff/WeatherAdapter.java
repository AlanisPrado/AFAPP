package com.example.apaff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;


import java.util.List;




public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    List<WeatherRecord> list;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(WeatherRecord r);
        void onLongClick(WeatherRecord r);
    }

    public WeatherAdapter(List<WeatherRecord> list, OnItemClickListener l) {
        this.list = list;
        this.listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView temp, note;

        public ViewHolder(View v) {
            super(v);
            temp = v.findViewById(R.id.txtTemp);
            note = v.findViewById(R.id.txtNote);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {

        WeatherRecord r = list.get(pos);

        h.temp.setText("🌡 " + r.temperature + "°C");
        h.note.setText("📝 " + r.note);

        h.itemView.setOnClickListener(v -> listener.onClick(r));
        h.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(r);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}