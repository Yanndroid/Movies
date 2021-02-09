package de.dlyt.yanndroid.movies.adapter;

import android.animation.LayoutTransition;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.Locale;

import de.dlyt.yanndroid.movies.MainActivity;
import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.dialog.MovieInfoDialog;
import de.dlyt.yanndroid.movies.dialog.TMDbInfoDialog;
import de.dlyt.yanndroid.movies.utilities.ItemViewModel;
import de.dlyt.yanndroid.movies.utilities.Movie;
import de.dlyt.yanndroid.movies.utilities.NetworkUtils;

public class MovieItemAdapter extends RecyclerView.Adapter<MovieItemAdapter.ViewHolder> {
    private ArrayList<HashMap<String, Object>> data;
    private HashMap<Integer, Boolean> m_expanded = new HashMap<Integer, Boolean>();
    private ArrayList<HashMap<String, Object>> fav_data;
    private android.content.Context context;

    private ItemViewModel itemViewModel;
    private FragmentActivity activity;


    public MovieItemAdapter(ArrayList<HashMap<String, Object>> data, Context context, FragmentActivity activity) {
        this.data = data;
        this.context = context;

        this.activity = activity;
        itemViewModel = new ViewModelProvider(activity).get(ItemViewModel.class);
    }

    @Override
    public MovieItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.movieitem_view, parent, false);
        return new ViewHolder(rowItem);
    }


    @Override
    public void onBindViewHolder(MovieItemAdapter.ViewHolder holder, int position) {
        if (data.get(position).containsKey("title")) {
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
    }


    public void single_item(MovieItemAdapter.ViewHolder holder, int position) {
        holder.single_item_card.setVisibility(View.VISIBLE);
        holder.multiple_item_card.setVisibility(View.GONE);


        /** Search and Filter */
        itemViewModel.getHashmap().observe(activity, hashMap -> {
            if (data.get(position).get("title").toString().toLowerCase().contains(hashMap.get("search")) && data.get(position).get("language").toString().toLowerCase().contains(hashMap.get("language")) && data.get(position).get("resolution").toString().toLowerCase().contains(hashMap.get("resolution"))) {
                holder.single_item_card.setVisibility(View.VISIBLE);
            } else {

                if (data.get(position).containsKey("group")) {
                    if (data.get(position).get("group").toString().toLowerCase().contains(hashMap.get("search")) && !hashMap.get("search").isEmpty()) {
                        holder.single_item_card.setVisibility(View.VISIBLE);
                    } else {
                        holder.single_item_card.setVisibility(View.GONE);
                    }
                } else {
                    holder.single_item_card.setVisibility(View.GONE);
                }
            }
        });


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
                MovieInfoDialog bottomSheetDialog = MovieInfoDialog.newInstance(data.get(position));
                bottomSheetDialog.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "tag");
            }
        });


        holder.single_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.moviedatalist != null) {
                    TMDbInfoDialog bottomSheetDialog = TMDbInfoDialog.newInstance(holder.moviedatalist, 0, true);
                    bottomSheetDialog.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "tag");
                } else {
                    if (data.get(position).containsKey("tmdbinfo")) {
                        if (data.get(position).get("tmdbinfo") != null) {
                            TMDbInfoDialog bottomSheetDialog = TMDbInfoDialog.newInstance((ArrayList<Movie>) data.get(position).get("tmdbinfo"), 0, true);
                            bottomSheetDialog.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "tag");
                        }
                    }
                }
            }
        });


        /** fav */
        SharedPreferences sharedPreferences = context.getSharedPreferences("lists", Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>() {
        }.getType();


        fav_data = new ArrayList<>();
        fav_data = gson.fromJson(sharedPreferences.getString("fav_list", "[]"), listType);

        for (int j = 0; j < fav_data.size(); j++) {
            if (this.data.get(position).get("title").equals(fav_data.get(j).get("title"))) {
                holder.bookmark_check.setChecked(true);
                break;
            }
        }

        holder.bookmark_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fav_data = new ArrayList<>();
                fav_data = gson.fromJson(sharedPreferences.getString("fav_list", "[]"), listType);
                if (isChecked) {
                    fav_data.add(data.get(position));
                } else {
                    for (int j = 0; j < fav_data.size(); j++) {
                        if (data.get(position).get("title").equals(fav_data.get(j).get("title"))) {
                            fav_data.remove(j);
                            break;
                        }
                    }
                }
                sharedPreferences.edit().putString("fav_list", gson.toJson(fav_data)).commit();
            }
        });


        /** TMDb */
        SharedPreferences sharedPreferences_settings;
        sharedPreferences_settings = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);


        HashMap<Integer, String> languages = new HashMap<>();
        languages.put(0, Locale.getDefault().toLanguageTag());
        languages.put(1, "en-US");
        languages.put(2, "de-DE");
        languages.put(3, "fr-FR");

        holder.moviedataurl = "https://api.themoviedb.org/3/search/movie?api_key=4ce769e35162474ccf8833d517f5285e&query=" + this.data.get(position).get("title").toString().replace(" ", "+") + "&language=" + languages.get(sharedPreferences_settings.getInt("language_spinner", 0));

        new FetchMovies(holder, position).execute();

    }

    public void multiple_item(MovieItemAdapter.ViewHolder holder, int position) {
        holder.single_item_card.setVisibility(View.GONE);
        holder.multiple_item_card.setVisibility(View.VISIBLE);

        if (data.get(position).containsKey("title")) {
            holder.item_multiple_title.setText(this.data.get(position).get("title").toString());

        }

        ArrayList<HashMap<String, Object>> series_list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tmp_list2 = new HashMap<String, Object>();
        for (int i = 0; i < data.get(position).size(); i++) {
            if (data.get(position).containsKey(String.valueOf(i))) {
                tmp_list2 = (HashMap<String, Object>) data.get(position).get(String.valueOf(i));
                series_list.add(tmp_list2);
            }
        }


        holder.multiple_movies_recyclerview.setLayoutManager(new LinearLayoutManager(this.context));
        holder.multiple_movies_recyclerview.setAdapter(new MovieItemAdapter(series_list, context, activity));


        if (!m_expanded.containsKey(position)) {
            m_expanded.put(position, false);
        }
        //set_multiple_item_expand(m_expanded.get(position), holder, position);


        holder.multiple_item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_multiple_item_expand(!m_expanded.get(position), holder, position);
            }
        });


        /** Search and Filter */
        itemViewModel.getHashmap().observe(activity, hashMap -> {

            Boolean childFiltermatch = false;
            for (HashMap<String, Object> child : series_list) {
                if (child.containsKey("title")) {
                    if (child.get("title").toString().toLowerCase().contains(hashMap.get("search"))) {
                        childFiltermatch = true;
                    } else {
                        childFiltermatch = false;
                        continue;
                    }
                }
                if (child.containsKey("language")) {
                    if (child.get("language").toString().toLowerCase().contains(hashMap.get("language"))) {
                        childFiltermatch = true;
                    } else {
                        childFiltermatch = false;
                        continue;
                    }
                }
                if (child.containsKey("resolution")) {
                    if (child.get("resolution").toString().toLowerCase().contains(hashMap.get("resolution"))) {
                        childFiltermatch = true;
                    } else {
                        childFiltermatch = false;
                        continue;
                    }
                }
                if (childFiltermatch) {
                    break;
                }
            }


            if (childFiltermatch) {
                holder.multiple_item_card.setVisibility(View.VISIBLE);
            } else {
                holder.multiple_item_card.setVisibility(View.GONE);
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
            holder.leon_nervt.setLayoutTransition(new LayoutTransition());
            holder.multiple_movies_recyclerview.setVisibility(View.VISIBLE);
            m_expanded.put(position, true);
            animator.setFloatValues(180);
        } else {
            holder.leon_nervt.setLayoutTransition(null);
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
        private LinearLayout leon_nervt;

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
            this.leon_nervt = view.findViewById(R.id.leon_nervt);

        }
    }

    public class FetchMovies extends AsyncTask<Void, Void, Void> {

        MovieItemAdapter.ViewHolder holder;
        int position;

        public FetchMovies(ViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;
        }


        @Override
        protected Void doInBackground(Void... voids) {


            holder.moviedatalist = new ArrayList<>();
            try {
                if (NetworkUtils.networkStatus(context)) {
                    holder.moviedatalist = NetworkUtils.fetchData(holder.moviedataurl);
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

                data.get(position).put("tmdbinfo", holder.moviedatalist);

                holder.movie = holder.moviedatalist.get(0);

                if (!MainActivity.datasaving) {
                    Picasso.get().load(holder.movie.getPosterPath()).placeholder(R.drawable.ic_no_cover).into(holder.item_cover);
                }

            }
        }
    }
}