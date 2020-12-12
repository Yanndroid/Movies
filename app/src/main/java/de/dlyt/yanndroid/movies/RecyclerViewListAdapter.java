package de.dlyt.yanndroid.movies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.ViewHolder> {
    private ArrayList<HashMap<String, Object>> data;
    public RecyclerViewListAdapter (ArrayList<HashMap<String, Object>> data){
        this.data = data;
    }

    @Override
    public RecyclerViewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.movieitemview, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(RecyclerViewListAdapter.ViewHolder holder, int position) {


        if(data.get(position).containsKey("title")){
            holder.item_title.setText(this.data.get(position).get("title").toString());

        }
        if(data.get(position).containsKey("size")){
            holder.item_info1.setText(this.data.get(position).get("size").toString());

        }
        if(data.get(position).containsKey("format")){
            holder.item_info2.setText(this.data.get(position).get("format").toString());

        }


    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView item_title;
        private TextView item_info1;
        private TextView item_info2;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.item_title = view.findViewById(R.id.item_title);
            this.item_info1 = view.findViewById(R.id.item_info1);
            this.item_info2 = view.findViewById(R.id.item_info2);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position : " + getLayoutPosition() + " text : " + this.item_title.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}