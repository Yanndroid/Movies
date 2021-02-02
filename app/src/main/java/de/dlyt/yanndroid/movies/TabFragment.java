package de.dlyt.yanndroid.movies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
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
import de.dlyt.yanndroid.movies.utilities.ItemViewModel;

public class TabFragment extends Fragment {

    private static final String ARG_COUNT = "param1";
    public static int[] listsize = new int[3];
    private ArrayList<HashMap<String, Object>> list;
    RecyclerView recyclerView;
    LinearLayout loadingView;
    MovieItemAdapter adapter;
    SharedPreferences sharedPreferences;
    private Integer current_tab;
    View fav_view;


    public TabFragment() {
    }

    public static TabFragment newInstance(Integer counter) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    public static int getlistsize(int position) {
        return listsize[position];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            current_tab = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** Code */

        sharedPreferences = getContext().getSharedPreferences("lists", Activity.MODE_PRIVATE);

        loadingView = view.findViewById(R.id.loadingView);

        initRecycler(view);

    }

    public void initRecycler(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);

        if (current_tab == 2) {

            fav_view = view;

            /*Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType();


            list = gson.fromJson(sharedPreferences.getString("fav_list", "[]"), listType);
            if (list != null) {
                recyclerView = view.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new MovieItemAdapter(list, getContext(), getActivity());
                recyclerView.setAdapter(adapter);
            }

            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    loadingView.setVisibility(View.GONE);
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });


            listsize[current_tab] = list.size();
            MainActivity.refreshcount(current_tab);*/

        } else {

            /*Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
            String[] shpreitems = {"movie_list", "series_list"};


            list = gson.fromJson(sharedPreferences.getString(shpreitems[current_tab], null), listType);
            if (list != null){
                recyclerView = view.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(new RecyclerViewListAdapter(list, getContext()));
            }*/


            String[] children = {"Movies", "Series"};
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(children[current_tab]);
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

                    listsize[current_tab] = list.size();
                    MainActivity.refreshcount(current_tab);

                    //sharedPreferences.edit().putString(shpreitems[current_tab] ,gson.toJson(list)).commit();

                    recyclerView = view.findViewById(R.id.recyclerview);

                    if (false && current_tab == 0) {
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }

                    adapter = new MovieItemAdapter(list, getContext(), getActivity());
                    recyclerView.setAdapter(adapter);

                    recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            loadingView.setVisibility(View.GONE);
                            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /** reload Favorites */
        if (current_tab == 2){
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType();


            list = gson.fromJson(sharedPreferences.getString("fav_list", "[]"), listType);
            if (list != null) {
                recyclerView = fav_view.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new MovieItemAdapter(list, getContext(), getActivity());
                recyclerView.setAdapter(adapter);
            }

            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    loadingView.setVisibility(View.GONE);
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            listsize[current_tab] = list.size();
            MainActivity.refreshcount(current_tab);
        }

    }
}