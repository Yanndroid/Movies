package de.dlyt.yanndroid.movies.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.movies.R;

public class ResolutionAdapter extends RecyclerView.Adapter<ResolutionAdapter.ViewHolder> {
    private ArrayList<HashMap<String, Object>> data;

    public ResolutionAdapter(ArrayList<HashMap<String, Object>> data) {
        this.data = data;
    }

    @Override
    public ResolutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.resolutionitem_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(ResolutionAdapter.ViewHolder holder, int position) {


        holder.name.setText(this.data.get(position).get("name").toString());
        holder.resos.setText(this.data.get(position).get("resoshort").toString());
        holder.resol.setText(this.data.get(position).get("resolong").toString());


    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView resos;
        private TextView resol;

        public ViewHolder(View view) {
            super(view);
            this.name = view.findViewById(R.id.name);
            this.resos = view.findViewById(R.id.resos);
            this.resol = view.findViewById(R.id.resol);
        }
    }
}