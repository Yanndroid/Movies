package de.dlyt.yanndroid.movies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class TabFragment extends Fragment {

    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private DatabaseReference mDatabase;
    private ArrayList<HashMap<String, Object>> list;

    public TabFragment() {
        // Required empty public constructor
    }

    public static TabFragment newInstance(Integer counter) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** Code */
        TextView textViewCounter = view.findViewById(R.id.moviename);

        textViewCounter.setText(counter.toString());

        if(counter == 0 || counter == 1){
            initRecycler(view, counter);

        }

    }



    public void initRecycler(View view, int tabnum){
        String[] tabnames = {"Movies","Series"};
        mDatabase = FirebaseDatabase.getInstance().getReference().child(tabnames[tabnum]);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                try {
                    GenericTypeIndicator<HashMap<String, Object>> ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    for(DataSnapshot child : snapshot.getChildren()){
                        HashMap<String, Object> map = child.getValue(ind);
                        list.add(map);
                    }
                } catch (Exception e) {
                    //nothing
                }



                //Item view
                TextView textViewCounter = view.findViewById(R.id.moviename);
                HashMap<String, Object> str = list.get(0);
                textViewCounter.setText(str.get("title").toString());
                //Item view


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}