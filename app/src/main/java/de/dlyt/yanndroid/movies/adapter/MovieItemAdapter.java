package de.dlyt.yanndroid.movies.adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.movies.Movie;
import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.dialog.MovieInfoDialog;
import de.dlyt.yanndroid.movies.utilities.NetworkUtils;

public class MovieItemAdapter extends RecyclerView.Adapter<MovieItemAdapter.ViewHolder> {
    private ArrayList<HashMap<String, Object>> data;
    private static HashMap<String, Object> datainfos;
    private HashMap<Integer, Boolean> m_expanded = new HashMap<Integer, Boolean>();

    ArrayList<HashMap<String, Object>> fav_list;

    private android.content.Context context;

    public MovieItemAdapter(ArrayList<HashMap<String, Object>> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public MovieItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.movieitem_view, parent, false);
        return new ViewHolder(rowItem);
    }


    @Override
    public void onBindViewHolder(MovieItemAdapter.ViewHolder holder, int position) {
        if (data.get(position).containsKey("type")) {
            switch (data.get(position).get("type").toString()) {
                case "single_item":
                    single_item(holder, position);
                    return;
                case "multiple_item":
                    multiple_item(holder, position);
                    return;
            }
        }
    }


    public void single_item(MovieItemAdapter.ViewHolder holder, int position) {
        holder.single_item_card.setVisibility(View.VISIBLE);
        holder.multiple_item_card.setVisibility(View.GONE);

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


        /** fav */
        SharedPreferences sharedPreferences;
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>() {
        }.getType();
        sharedPreferences = context.getSharedPreferences("lists", Activity.MODE_PRIVATE);

        fav_list = new ArrayList<>();
        fav_list = gson.fromJson(sharedPreferences.getString("fav_list", "[]"), listType);

        for (int j = 0; j < fav_list.size(); j++) {
            if (this.data.get(position).equals(fav_list.get(j))) {
                holder.bookmark_check.setChecked(true);
                break;
            }
        }

        holder.bookmark_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fav_list = new ArrayList<>();
                fav_list = gson.fromJson(sharedPreferences.getString("fav_list", "[]"), listType);
                if (isChecked) {
                    fav_list.add(data.get(position));
                } else {
                    for (int j = 0; j < fav_list.size(); j++) {
                        if (data.get(position).equals(fav_list.get(j))) {
                            fav_list.remove(j);
                            break;
                        }
                    }
                }
                sharedPreferences.edit().putString("fav_list", gson.toJson(fav_list)).commit();
            }
        });


        /** TMDb */


        holder.moviedataurl = "https://api.themoviedb.org/3/search/movie?api_key=4ce769e35162474ccf8833d517f5285e&query=" + this.data.get(position).get("title").toString().replace(" ", "+");

        new FetchMovies(holder).execute();


    }

    public class FetchMovies extends AsyncTask<Void, Void, Void> {

        MovieItemAdapter.ViewHolder holder;

        public FetchMovies(MovieItemAdapter.ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {


            holder.moviedatalist = new ArrayList<>();
            try {
                if (NetworkUtils.networkStatus(context)) {
                    holder.moviedatalist = NetworkUtils.fetchData(holder.moviedataurl); //Get popular movies

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("No Connection");
                    dialog.setCancelable(false);
                    dialog.show();
                }
            } catch (IOException e) {
                e.printStackTrace();


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if (holder.moviedatalist.size() != 0) {
                holder.movie = holder.moviedatalist.get(0);
                Picasso.get().load(holder.movie.getPosterPath()).resize(300, 0).placeholder(R.drawable.ic_no_cover).into(holder.item_cover);
            }
        }
    }


    public void multiple_item(MovieItemAdapter.ViewHolder holder, int position) {
        holder.single_item_card.setVisibility(View.GONE);
        holder.multiple_item_card.setVisibility(View.VISIBLE);

        if (data.get(position).containsKey("title")) {
            holder.item_multiple_title.setText(this.data.get(position).get("title").toString());

        }


        HashMap<String, Object> tmp_list = new HashMap<String, Object>();
        tmp_list = data.get(position);
        if (tmp_list.containsKey("title")) {
            tmp_list.remove("title");
        }
        if (tmp_list.containsKey("type")) {
            tmp_list.remove("type");
        }

        ArrayList<HashMap<String, Object>> series_list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tmp_list2 = new HashMap<String, Object>();
        for (int i = 0; i < tmp_list.size(); i++) {
            tmp_list2 = (HashMap<String, Object>) tmp_list.get(String.valueOf(i));
            series_list.add(tmp_list2);
        }


        holder.multiple_movies_recyclerview.setLayoutManager(new LinearLayoutManager(this.context));
        //holder.multiple_movies_recyclerview.setLayoutManager(new GridLayoutManager(context, 2));
        holder.multiple_movies_recyclerview.setAdapter(new MovieItemAdapter(series_list, context));


        if (!m_expanded.containsKey(position)) {
            m_expanded.put(position, false);
        }
        set_multiple_item_expand(m_expanded.get(position), holder, position);


        holder.multiple_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_multiple_item_expand(!m_expanded.get(position), holder, position);
            }
        });
    }

    public void set_multiple_item_expand(boolean setexpanded, ViewHolder holder, Integer position) {
        ObjectAnimator animator = new ObjectAnimator();
        animator.setTarget(holder.dropdown_image);
        animator.setPropertyName("rotation");
        animator.setDuration(500);
        animator.setInterpolator(new BounceInterpolator());
        if (setexpanded) {
            holder.multiple_movies_recyclerview.setVisibility(View.VISIBLE);
            m_expanded.put(position, true);
            animator.setFloatValues(180);
        } else {
            holder.multiple_movies_recyclerview.setVisibility(View.GONE);
            m_expanded.put(position, false);
            animator.setFloatValues(0);
        }
        animator.start();
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

        private String moviedataurl;
        private ArrayList<Movie> moviedatalist;
        private Movie movie;

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

        }
    }

    public static HashMap<String, Object> getData() {
        return datainfos;
    }
}