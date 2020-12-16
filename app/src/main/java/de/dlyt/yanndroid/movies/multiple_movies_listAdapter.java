package de.dlyt.yanndroid.movies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.dialogs.MovieInfoDialog;
import de.dlyt.yanndroid.movies.dialogs.mMovieInfoDialog;

public class multiple_movies_listAdapter extends RecyclerView.Adapter<multiple_movies_listAdapter.ViewHolder> {
    private HashMap<String, Object> data;
    private static HashMap<String, Object> datainfos;


    public multiple_movies_listAdapter(HashMap<String, Object> data) {
        this.data = data;
    }
    @Override
    public multiple_movies_listAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_movies_itemview, parent, false);
        return new ViewHolder(rowItem);
    }






    @Override
    public void onBindViewHolder(multiple_movies_listAdapter.ViewHolder holder, int position) {


        HashMap<String, Object> list = new HashMap<String, Object>();

        list = (HashMap<String, Object>) data.get(String.valueOf(position));


        if (list.containsKey("title")) {
            holder.item_title.setText(list.get("title").toString());

        }
        if (list.containsKey("language")) {
            holder.item_language.setText(list.get("language").toString());

        }
        if (list.containsKey("resolution")) {
            holder.item_resolution.setText(list.get("resolution").toString());

        }

        holder.infoimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datainfos = (HashMap<String, Object>) data.get(String.valueOf(position));
                mMovieInfoDialog bottomSheetDialog = mMovieInfoDialog.newInstance();
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







    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView item_title;
        private TextView item_language;
        private TextView item_resolution;
        private ImageView infoimage;
        private ImageView item_cover;
        private CheckBox bookmark_check;

        public ViewHolder(View view) {
            super(view);
            this.item_title = view.findViewById(R.id.m_item_title);
            this.item_language = view.findViewById(R.id.m_item_language);
            this.item_resolution = view.findViewById(R.id.m_item_resolution);
            this.infoimage = view.findViewById(R.id.m_info_image);
            this.item_cover = view.findViewById(R.id.m_item_cover);
            this.bookmark_check = view.findViewById(R.id.m_bookmark_check);

        }
    }

    public static HashMap<String, Object> getData() {
        return datainfos;
    }
}