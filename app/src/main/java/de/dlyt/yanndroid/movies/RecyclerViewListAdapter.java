package de.dlyt.yanndroid.movies;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.movies.dialogs.MovieInfoDialog;

public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.ViewHolder> {
    private ArrayList<HashMap<String, Object>> data;
    private static HashMap<String, Object> datainfos;
    private HashMap<Integer, Boolean> m_expanded = new HashMap<Integer, Boolean>();
    private HashMap<Integer, Boolean> s_expanded = new HashMap<Integer, Boolean>();

    private android.content.Context context;

    public RecyclerViewListAdapter(ArrayList<HashMap<String, Object>> data, Context context) {
        this.data = data;
        this.context = context;
    }
    @Override
    public RecyclerViewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.movieitemview, parent, false);
        return new ViewHolder(rowItem);
    }






    @Override
    public void onBindViewHolder(RecyclerViewListAdapter.ViewHolder holder, int position) {
        if (data.get(position).containsKey("type")) {
            switch (data.get(position).get("type").toString()){
                case "single_movie": load_single_movie(holder, position);return;
                case "movie_series": load_multiple_movie(holder, position);return;
                case "series": load_series(holder, position);return;

            }
        }

    }





    public void load_single_movie(RecyclerViewListAdapter.ViewHolder holder, int position){
        holder.single_item_card.setVisibility(View.VISIBLE);
        holder.multiple_item_card.setVisibility(View.GONE);
        holder.series_item_card.setVisibility(View.GONE);

        if (data.get(position).containsKey("title")) {
            holder.item_title.setText(this.data.get(position).get("title").toString());

        }
        if (data.get(position).containsKey("language")) {
            holder.item_language.setText(this.data.get(position).get("language").toString());

        }
        if (data.get(position).containsKey("resolution")) {
            holder.item_resolution.setText(this.data.get(position).get("resolution").toString());

        }

        holder.infoimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datainfos = data.get(position);
                MovieInfoDialog bottomSheetDialog = MovieInfoDialog.newInstance();
                bottomSheetDialog.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "tag");
            }
        });

        holder.bookmark_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Snackbar.make(holder.bookmark_check, "Bookmark currently not available", Snackbar.LENGTH_SHORT).show();
                }else{

                }
            }
        });

    }


    public void load_multiple_movie(RecyclerViewListAdapter.ViewHolder holder, int position){
        holder.single_item_card.setVisibility(View.GONE);
        holder.multiple_item_card.setVisibility(View.VISIBLE);
        holder.series_item_card.setVisibility(View.GONE);

        if (data.get(position).containsKey("title")) {
            holder.item_multiple_title.setText(this.data.get(position).get("title").toString());

        }


        HashMap<String, Object> tmp_list = new HashMap<String, Object>();
        tmp_list = data.get(position);
        if(tmp_list.containsKey("title")){
            tmp_list.remove("title");
        }
        if(tmp_list.containsKey("type")){
            tmp_list.remove("type");
        }



        holder.multiple_movies_recyclerview.setLayoutManager(new LinearLayoutManager(this.context));
        holder.multiple_movies_recyclerview.setAdapter(new multiple_movies_listAdapter(tmp_list));


        if(!m_expanded.containsKey(position)){
            m_expanded.put(position, false);
        }
        set_m_movie_expanded(m_expanded.get(position), holder, position);


        holder.multiple_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_m_movie_expanded(!m_expanded.get(position), holder, position);
            }
        });
    }

    public void set_m_movie_expanded(boolean setexpanded, ViewHolder holder, Integer position){
        ObjectAnimator animator = new ObjectAnimator();
        animator.setTarget(holder.dropdown_image);
        animator.setPropertyName("rotation");
        animator.setDuration(500);
        animator.setInterpolator(new BounceInterpolator());
        if(setexpanded){
            holder.multiple_movies_recyclerview.setVisibility(View.VISIBLE);
            m_expanded.put(position, true);
            animator.setFloatValues(180);
        }else {
            holder.multiple_movies_recyclerview.setVisibility(View.GONE);
            m_expanded.put(position, false);
            animator.setFloatValues(0);
        }
        animator.start();
    }

    public void set_series_expanded(boolean setexpanded, ViewHolder holder, Integer position){
        ObjectAnimator animator = new ObjectAnimator();
        animator.setTarget(holder.dropdown_image_series);
        animator.setPropertyName("rotation");
        animator.setDuration(500);
        animator.setInterpolator(new BounceInterpolator());
        if(setexpanded){
            holder.series_recyclerview.setVisibility(View.VISIBLE);
            s_expanded.put(position, true);
            animator.setFloatValues(180);
        }else {
            holder.series_recyclerview.setVisibility(View.GONE);
            s_expanded.put(position, false);
            animator.setFloatValues(0);
        }
        animator.start();
    }


    public void load_series(RecyclerViewListAdapter.ViewHolder holder, int position){
        holder.single_item_card.setVisibility(View.GONE);
        holder.multiple_item_card.setVisibility(View.GONE);
        holder.series_item_card.setVisibility(View.VISIBLE);

        if (data.get(position).containsKey("title")) {
            holder.item_series_title.setText(this.data.get(position).get("title").toString());

        }


        HashMap<String, Object> tmp_list = new HashMap<String, Object>();
        tmp_list = data.get(position);
        if(tmp_list.containsKey("title")){
            tmp_list.remove("title");
        }
        if(tmp_list.containsKey("type")){
            tmp_list.remove("type");
        }


        ArrayList<HashMap<String, Object>> series_list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tmp_list2 = new HashMap<String, Object>();

        for(int i = 0; i <tmp_list.size(); i++){

            tmp_list2 = (HashMap<String, Object>) tmp_list.get(String.valueOf(i));
            series_list.add(tmp_list2);
        }


        holder.series_recyclerview.setLayoutManager(new LinearLayoutManager(this.context));
        holder.series_recyclerview.setAdapter(new RecyclerViewListAdapter(series_list, context));


        if(!s_expanded.containsKey(position)){
            s_expanded.put(position, false);
        }
        set_series_expanded(s_expanded.get(position), holder, position);


        holder.series_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_series_expanded(!s_expanded.get(position), holder, position);
            }
        });

    }





    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView single_item_card;
        private TextView item_title;
        private TextView item_language;
        private TextView item_resolution;
        private ImageView infoimage;
        private ImageView item_cover;
        private CheckBox bookmark_check;


        private MaterialCardView multiple_item_card;
        private TextView item_multiple_title;
        private ImageView dropdown_image;
        private RecyclerView multiple_movies_recyclerview;


        private MaterialCardView series_item_card;
        private TextView item_series_title;
        private ImageView dropdown_image_series;
        private RecyclerView series_recyclerview;


        public ViewHolder(View view) {
            super(view);

            this.single_item_card = view.findViewById(R.id.single_item_card);
            this.item_title = view.findViewById(R.id.item_title);
            this.item_language = view.findViewById(R.id.item_language);
            this.item_resolution = view.findViewById(R.id.item_resolution);
            this.infoimage = view.findViewById(R.id.info_image);
            this.item_cover = view.findViewById(R.id.item_cover);
            this.bookmark_check = view.findViewById(R.id.bookmark_check);

            this.multiple_item_card = view.findViewById(R.id.multiple_item_card);
            this.item_multiple_title = view.findViewById(R.id.item_multiple_title);
            this.dropdown_image = view.findViewById(R.id.dropdown_image);
            this.multiple_movies_recyclerview = view.findViewById(R.id.multiple_movies_recyclerview);

            this.series_item_card = view.findViewById(R.id.series_item_card);
            this.item_series_title = view.findViewById(R.id.item_series_title);
            this.dropdown_image_series = view.findViewById(R.id.dropdown_image_series);
            this.series_recyclerview = view.findViewById(R.id.series_recyclerview);
        }
    }

    public static HashMap<String, Object> getData() {
        return datainfos;
    }
}