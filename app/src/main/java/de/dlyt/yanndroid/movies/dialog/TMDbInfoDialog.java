package de.dlyt.yanndroid.movies.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.io.CharStreams;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.adapter.GenreAdapter;
import de.dlyt.yanndroid.movies.utilities.Movie;
import kotlin.text.Charsets;


public class TMDbInfoDialog extends BottomSheetDialogFragment {

    private static ArrayList<Movie> moviedatalist;
    private static int whichtoload;
    private static Boolean first;
    private Movie movie;

    public static TMDbInfoDialog newInstance(ArrayList<Movie> moviedatalist, int whichtoload, Boolean first) {
        TMDbInfoDialog fragment = new TMDbInfoDialog();
        TMDbInfoDialog.moviedatalist = moviedatalist;
        TMDbInfoDialog.whichtoload = whichtoload;
        TMDbInfoDialog.first = first;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.tmdbinfo_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        Button close = dialog.findViewById(R.id.close);

        ImageView backdrop_image = dialog.findViewById(R.id.backdrop_image);
        ImageView poster_image = dialog.findViewById(R.id.poster_image);
        TextView original_title = dialog.findViewById(R.id.original_title);
        TextView overview = dialog.findViewById(R.id.overview);
        TextView release_date = dialog.findViewById(R.id.release_date);

        View list_all = dialog.findViewById(R.id.list_all);

        if (moviedatalist.size() > 1 && first) {
            list_all.setVisibility(View.VISIBLE);
        }

        list_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlternativeDialog bottomSheetDialog = AlternativeDialog.newInstance(moviedatalist);
                bottomSheetDialog.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "tag");
            }
        });


        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);

        if (moviedatalist.size() != 0) {
            try {
                movie = moviedatalist.get(whichtoload);
                Picasso.get().load(movie.getBackdropPath()).placeholder(R.drawable.ic_no_backdrop).into(backdrop_image);
                Picasso.get().load(movie.getPosterPath()).placeholder(R.drawable.ic_no_cover).into(poster_image);

                original_title.setText(movie.getOriginalTitle());
                overview.setText(movie.getOverview());
                release_date.setText(movie.getReleaseDate());

                ratingBar.setRating(Float.valueOf(movie.getVoteAverage()) / 2);

                ProgressBar loading = dialog.findViewById(R.id.loading);
                loading.setVisibility(View.VISIBLE);

                new setGenre(movie.getGenreIds(), dialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                new loadTrailer(movie.getId(), dialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e) {
                overview.setText(R.string.please_wait_and_retry);
            }
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }


    public class loadTrailer extends AsyncTask<Long, Void, String> {

        Long id;
        Dialog dialog;
        String trailerUrl;

        public loadTrailer(Long id, Dialog dialog) {
            this.id = id;
            this.dialog = dialog;
        }

        @Override
        protected String doInBackground(Long... longs) {

            HashMap<Integer, String> languages = new HashMap<>();
            languages.put(0, Locale.getDefault().toLanguageTag());
            languages.put(1, "en-US");
            languages.put(2, "de-DE");
            languages.put(3, "fr-FR");

            SharedPreferences sharedPreferences_settings;
            sharedPreferences_settings = getContext().getSharedPreferences("settings", Activity.MODE_PRIVATE);

            try {
                URL url;
                url = new URL("https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=4ce769e35162474ccf8833d517f5285e&language=" + languages.get(sharedPreferences_settings.getInt("language_spinner", 0)));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String results = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));

                if (new JSONObject(results).getJSONArray("results").length() != 0) {
                    trailerUrl = /*"https://www.youtube.com/watch?v=" +*/ new JSONObject(results).getJSONArray("results").getJSONObject(0).getString("key");
                }

                inputStream.close();
                return trailerUrl;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            YouTubePlayerView youTubePlayerView = dialog.findViewById(R.id.youtubeView);
            TextView noTrailer = dialog.findViewById(R.id.noTrailer);
            ProgressBar loading = dialog.findViewById(R.id.loading);
            loading.setVisibility(View.GONE);

            if (trailerUrl != null) {
                noTrailer.setVisibility(View.GONE);
                youTubePlayerView.setVisibility(View.VISIBLE);
                getLifecycle().addObserver(youTubePlayerView);
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.cueVideo(trailerUrl, 0);
                    }
                });
            } else {
                noTrailer.setVisibility(View.VISIBLE);
                youTubePlayerView.setVisibility(View.GONE);
            }
        }
    }

    public class setGenre extends AsyncTask<ArrayList<Integer>, Void, HashMap<Integer, String>> {

        ArrayList<Integer> input;
        Dialog dialog;

        public setGenre(ArrayList<Integer> input, Dialog dialog) {
            this.input = input;
            this.dialog = dialog;
        }

        @Override
        protected HashMap<Integer, String> doInBackground(ArrayList<Integer>... arrayLists) {
            SharedPreferences sharedPreferences_settings;
            sharedPreferences_settings = getContext().getSharedPreferences("settings", Activity.MODE_PRIVATE);
            HashMap<Integer, String> languages = new HashMap<>();
            languages.put(0, Locale.getDefault().toLanguageTag());
            languages.put(1, "en-US");
            languages.put(2, "de-DE");
            languages.put(3, "fr-FR");

            HashMap<Integer, String> IntToStringList = new HashMap<>();

            try {
                URL url;
                url = new URL("https://api.themoviedb.org/3/genre/movie/list?api_key=4ce769e35162474ccf8833d517f5285e&language=" + languages.get(sharedPreferences_settings.getInt("language_spinner", 0)));
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String genres = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));

                for (int i = 0; i < new JSONObject(genres).getJSONArray("genres").length(); i++) {
                    IntToStringList.put(new JSONObject(genres).getJSONArray("genres").getJSONObject(i).getInt("id"), new JSONObject(genres).getJSONArray("genres").getJSONObject(i).getString("name"));
                }

                inputStream.close();

                return IntToStringList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(HashMap<Integer, String> genrelist) {
            super.onPostExecute(genrelist);

            if (genrelist != null) {
                ArrayList<String> output = new ArrayList<>();
                RecyclerView genreRecycler = dialog.findViewById(R.id.genreRecycler);
                for (Integer integer : input) {
                    if (genrelist.containsKey(integer)) {
                        output.add(genrelist.get(integer));
                    }
                }
                genreRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                genreRecycler.setAdapter(new GenreAdapter(output));
            }


        }
    }

}