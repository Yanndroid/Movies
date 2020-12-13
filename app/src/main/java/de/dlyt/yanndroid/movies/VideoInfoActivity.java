package de.dlyt.yanndroid.movies;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoInfoActivity extends AppCompatActivity {

    RecyclerView resolutionrecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_info);
        initToolbar();
        initRecycler();

    }


    public void initToolbar() {
        /** Def */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout AppBar = findViewById(R.id.app_bar);
        TextView expanded_title = findViewById(R.id.expanded_title);
        TextView title = findViewById(R.id.title);

        /** 1/3 of the Screen */
        ViewGroup.LayoutParams layoutParams = AppBar.getLayoutParams();
        layoutParams.height = (int) ((double) this.getResources().getDisplayMetrics().heightPixels / 2.6);


        /** Collapsing */
        AppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = (AppBar.getY() / AppBar.getTotalScrollRange());
                expanded_title.setAlpha(1 - (percentage * 2 * -1));
                title.setAlpha(percentage * -1);
            }
        });


        /** Back */
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    public void initRecycler() {


        resolutionrecycler = findViewById(R.id.resolutionview);
        resolutionrecycler.setLayoutManager(new LinearLayoutManager(this));
        resolutionrecycler.setAdapter(new ResolutionAdapter(generateData()));

    }

    private ArrayList<HashMap<String, Object>> generateData() {
        ArrayList<HashMap<String, Object>> resolist = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();

        map = new HashMap<>();
        map.put("name", "FUHD");
        map.put("resolong", "7680x4320");
        map.put("resoshort", "4320p / 8k");
        resolist.add(map);

        map = new HashMap<>();
        map.put("name", "6k");
        map.put("resolong", "6144x3456");
        map.put("resoshort", "3456p / 6k");
        resolist.add(map);

        map = new HashMap<>();
        map.put("name", "WUHD");
        map.put("resolong", "5120x2880");
        map.put("resoshort", "2880p / 5k");
        resolist.add(map);

        map = new HashMap<>();
        map.put("name", "UHD");
        map.put("resolong", "3840x2160");
        map.put("resoshort", "2160p / 4k");
        resolist.add(map);

        map = new HashMap<>();
        map.put("name", "QHD");
        map.put("resolong", "2560x1440");
        map.put("resoshort", "1440p / 2k");
        resolist.add(map);

        map = new HashMap<>();
        map.put("name", "FHD");
        map.put("resolong", "1920x1080");
        map.put("resoshort", "1080p");
        resolist.add(map);

        map = new HashMap<>();
        map.put("name", "HD");
        map.put("resolong", "1080x720");
        map.put("resoshort", "720p");
        resolist.add(map);

        map = new HashMap<>();
        map.put("name", "SD");
        map.put("resolong", "720x576");
        map.put("resoshort", "576p");
        resolist.add(map);

        map = new HashMap<>();
        map.put("name", "VGA");
        map.put("resolong", "640x480");
        map.put("resoshort", "480p");
        resolist.add(map);

        return resolist;
    }


}