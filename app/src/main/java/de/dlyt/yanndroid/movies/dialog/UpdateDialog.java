package de.dlyt.yanndroid.movies.dialog;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.movies.AppInfoActivity;
import de.dlyt.yanndroid.movies.R;
import de.dlyt.yanndroid.movies.UpdateApp;

public class UpdateDialog extends BottomSheetDialogFragment {

    private DatabaseReference mDatabase;
    private ArrayList<HashMap<String, Object>> updateinfo;

    public static UpdateDialog newInstance() {
        UpdateDialog fragment = new UpdateDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.update_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        TextView nver = dialog.findViewById(R.id.nver);
        TextView cver = dialog.findViewById(R.id.cver);
        Button update = dialog.findViewById(R.id.update);
        Button cancel = dialog.findViewById(R.id.cancel);


        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            int versionC = pInfo.versionCode;
            String versionN = pInfo.versionName;
            cver.setText(getString(R.string.installed_version) + " " + versionN);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("AndroidApp");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    updateinfo = new ArrayList<>();
                    try {
                        GenericTypeIndicator<HashMap<String, Object>> ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };
                        for (DataSnapshot child : snapshot.getChildren()) {
                            HashMap<String, Object> map = child.getValue(ind);
                            updateinfo.add(map);
                        }
                    } catch (Exception e) {
                        //nothing
                    }


                    nver.setText(getString(R.string.newest_version) + " " + updateinfo.get(0).get("name").toString());

                    if (versionC < Double.parseDouble(updateinfo.get(0).get("code").toString())) {
                        update.setText(R.string.update);
                    } else {
                        update.setText(R.string.download);
                    }
                    update.setEnabled(true);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateApp.DownloadAndInstall(AppInfoActivity.get_Context(), "https://github.com/Yanndroid/Movies/raw/master/app/release/app-release.apk", "Movies_" + updateinfo.get(0).get("name").toString() + ".apk", "Movies Update", updateinfo.get(0).get("name").toString());

                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}