package de.dlyt.yanndroid.movies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.dialog.TMDbInfoDialog;
import de.dlyt.yanndroid.movies.utilities.Movie;

public class AlternativeListAdapter extends RecyclerView.Adapter<AlternativeListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Movie> moviedatalist;

    public AlternativeListAdapter(ArrayList<Movie> moviedatalist, Context context) {
        this.moviedatalist = moviedatalist;
        this.context = context;
    }


    @Override
    public AlternativeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.alternativeitem_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(AlternativeListAdapter.ViewHolder holder, int position) {


        holder.movie = moviedatalist.get(position);
        holder.title.setText(holder.movie.getOriginalTitle());
        holder.info.setText(holder.movie.getOverview());
        Picasso.get().load(holder.movie.getPosterPath()).placeholder(R.drawable.ic_no_cover).into(holder.cover);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TMDbInfoDialog bottomSheetDialog = TMDbInfoDialog.newInstance(moviedatalist, position, false);
                bottomSheetDialog.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "tag");
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.moviedatalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View card;
        private TextView title;
        private TextView info;
        private ImageView cover;
        private Movie movie;

        public ViewHolder(View view) {
            super(view);
            this.card = view.findViewById(R.id.item_card);
            this.title = view.findViewById(R.id.item_title);
            this.info = view.findViewById(R.id.item_info);
            this.cover = view.findViewById(R.id.item_cover);
        }
    }
}