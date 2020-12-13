package de.dlyt.yanndroid.movies.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.RecyclerViewListAdapter;

public class MovieInfoDialog extends BottomSheetDialogFragment {

    private DatabaseReference mDatabase;

    public static MovieInfoDialog newInstance() {
        MovieInfoDialog fragment = new MovieInfoDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.movieinfo_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        Button close = dialog.findViewById(R.id.close);

        TextView ititle = dialog.findViewById(R.id.ititle);
        TextView ilanguage = dialog.findViewById(R.id.ilanguage);
        TextView isubtitle = dialog.findViewById(R.id.isubtitle);
        TextView iresolution = dialog.findViewById(R.id.iresolution);
        TextView iduration = dialog.findViewById(R.id.iduration);
        TextView isize = dialog.findViewById(R.id.isize);
        TextView iformat = dialog.findViewById(R.id.iformat);

        HashMap<String, Object> infos = RecyclerViewListAdapter.getData();

        ititle.setText(infos.get("title").toString());
        ilanguage.setText(infos.get("language").toString());
        isubtitle.setText(infos.get("subtitle").toString());
        iresolution.setText(infos.get("resolution").toString());
        iduration.setText(infos.get("duration").toString());
        isize.setText(infos.get("size").toString());
        iformat.setText(infos.get("format").toString());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}