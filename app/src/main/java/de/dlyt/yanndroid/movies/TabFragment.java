package de.dlyt.yanndroid.movies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import de.dlyt.yanndroid.movies.adapter.MovieItemAdapter;

public class TabFragment extends Fragment {

    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private DatabaseReference mDatabase;
    private static ArrayList<HashMap<String, Object>> list;
    public static int[] listsize = new int[3];
    RecyclerView recyclerView;
    MovieItemAdapter adapter;
    SharedPreferences sharedPreferences;


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
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** Code */

        sharedPreferences = getContext().getSharedPreferences("lists", Activity.MODE_PRIVATE);

        initRecycler(view, counter);

    }

    public void initRecycler(View view, int counter) {
        recyclerView = view.findViewById(R.id.recyclerview);

        if (counter == 2) {

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType();


            list = gson.fromJson(sharedPreferences.getString("fav_list", "[]"), listType);
            if (list != null) {
                recyclerView = view.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //recyclerView.setAdapter(new MovieItemAdapter(list, getContext()));
                adapter = new MovieItemAdapter(list, getContext());
                recyclerView.setAdapter(adapter);
            }

            listsize[counter] = list.size();
            MainActivity.refreshcount();
        } else {

            /*Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
            String[] shpreitems = {"movie_list", "series_list"};


            list = gson.fromJson(sharedPreferences.getString(shpreitems[counter], null), listType);
            if (list != null){
                recyclerView = view.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(new RecyclerViewListAdapter(list, getContext()));
            }*/


            String[] children = {"Movies", "Series"};
            mDatabase = FirebaseDatabase.getInstance().getReference().child(children[counter]);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list = new ArrayList<>();
                    try {
                        GenericTypeIndicator<HashMap<String, Object>> ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };
                        for (DataSnapshot child : snapshot.getChildren()) {
                            HashMap<String, Object> map = child.getValue(ind);
                            list.add(map);
                        }
                    } catch (Exception e) {
                        //nothing
                    }

                    listsize[counter] = list.size();
                    MainActivity.refreshcount();

                    //sharedPreferences.edit().putString(shpreitems[counter] ,gson.toJson(list)).commit();

                    recyclerView = view.findViewById(R.id.recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    //recyclerView.setAdapter(new MovieItemAdapter(list, getContext()));
                    adapter = new MovieItemAdapter(list, getContext());
                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public static int getlistsize(int position) {
        return listsize[position];
    }
}