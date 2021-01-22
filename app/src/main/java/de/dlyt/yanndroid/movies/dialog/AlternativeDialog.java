package de.dlyt.yanndroid.movies.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.adapter.AlternativeListAdapter;
import de.dlyt.yanndroid.movies.adapter.MovieItemAdapter;
import de.dlyt.yanndroid.movies.utilities.Movie;

public class AlternativeDialog extends BottomSheetDialogFragment {

    private static ArrayList<Movie> moviedatalist;

    public static AlternativeDialog newInstance(ArrayList<Movie> moviedatalist) {
        AlternativeDialog fragment = new AlternativeDialog();
        AlternativeDialog.moviedatalist = moviedatalist;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.alternative_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ImageView close = dialog.findViewById(R.id.close);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerview);

        moviedatalist.remove(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new AlternativeListAdapter(moviedatalist, getContext()));



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}