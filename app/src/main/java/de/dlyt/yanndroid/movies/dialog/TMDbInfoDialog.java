package de.dlyt.yanndroid.movies.dialog;


/**
 * todo:
 * genre
 */

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.utilities.Movie;

public class TMDbInfoDialog extends BottomSheetDialogFragment {

    private static ArrayList<Movie> moviedatalist;
    private static int whichtoload;
    private Movie movie;

    public static TMDbInfoDialog newInstance(ArrayList<Movie> moviedatalist, int whichtoload) {
        TMDbInfoDialog fragment = new TMDbInfoDialog();
        TMDbInfoDialog.moviedatalist = moviedatalist;
        TMDbInfoDialog.whichtoload = whichtoload;
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

        if (moviedatalist.size() > 1 && whichtoload == 0) {
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
            movie = moviedatalist.get(whichtoload);
            Picasso.get().load(movie.getBackdropPath()).placeholder(R.drawable.ic_no_backdrop).into(backdrop_image);
            Picasso.get().load(movie.getPosterPath()).placeholder(R.drawable.ic_no_cover).into(poster_image);

            original_title.setText(movie.getOriginalTitle());
            overview.setText(movie.getOverview());
            release_date.setText(movie.getReleaseDate());


            ratingBar.setRating(Float.valueOf(movie.getVoteAverage()) / 2);

            new getTrailer(movie.getId(), dialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    public class getTrailer extends AsyncTask<Long, Void, String> {

        Long id;
        Dialog dialog;
        String trailerUrl;

        public getTrailer(Long id, Dialog dialog) {
            this.id = id;
            this.dialog = dialog;
        }

        @Override
        protected String doInBackground(Long... longs) {

            try {
                URL url;
                url = new URL("https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=4ce769e35162474ccf8833d517f5285e");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String results = IOUtils.toString(inputStream);

                if (new JSONObject(results).getJSONArray("results").length() != 0) {
                    trailerUrl = "https://www.youtube.com/watch?v=" + new JSONObject(results).getJSONArray("results").getJSONObject(0).getString("key");
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

            View trailer_button = dialog.findViewById(R.id.trailer_button);

            if (trailerUrl != null) {
                trailer_button.setVisibility(View.VISIBLE);
                trailer_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)));
                    }
                });
            } else {
                trailer_button.setVisibility(View.GONE);
            }
        }


    }

}