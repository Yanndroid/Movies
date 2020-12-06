package de.dlyt.yanndroid.movies;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ScrollingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        settilte("Movies");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1000);
        }

        /** collapsing Toolbar */
        initToolbar();

        /** ViewPager */
        initViewPager();

        /** Search */
        ImageView cancelseach = findViewById(R.id.cancelsearch);
        EditText searchinput = findViewById(R.id.searchinput);
        cancelseach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchinput.setText("");
                setsearching(false);
            }
        });




    }



    public void initToolbar(){
        /** Def */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout AppBar = findViewById(R.id.app_bar);

        TextView expanded_title = findViewById(R.id.expanded_title);
        TextView expanded_subtitle = findViewById(R.id.expanded_subtitle);
        TextView collapsed_title = findViewById(R.id.collapsed_title);

        /** 1/3 of the Screen */
        ViewGroup.LayoutParams layoutParams = AppBar.getLayoutParams();
        layoutParams.height = (int)((double)this.getResources().getDisplayMetrics().heightPixels / 2.4);


        /** Collapsing */
        AppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = (AppBar.getY() / AppBar.getTotalScrollRange());
                expanded_title.setAlpha(1 - (percentage * 2 * -1));
                expanded_subtitle.setAlpha(1 - (percentage * 2 * -1));
                collapsed_title.setAlpha(percentage * -1);
            }
        });

    }

    public void initViewPager(){
        String[] tabnames = {"Movies","Series","Favorites"};
        int[] tabicons = {R.drawable.ic_movie,R.drawable.ic_serie,R.drawable.ic_bookmark};
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText(tabnames[position]);
                        tab.setIcon(tabicons[position]);
                    }
                }).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                settilte(tabnames[tab.getPosition()]);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setsearching(Boolean searching){
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView collapsed_title = findViewById(R.id.collapsed_title);
        CardView searchview = findViewById(R.id.searchview);
        EditText searchinput = findViewById(R.id.searchinput);

        if(searching){
            toolbar.getMenu().findItem(R.id.search).setVisible(false);
            toolbar.getMenu().findItem(R.id.filter).setVisible(false);
            searchview.setVisibility(View.VISIBLE);
            searchinput.setEnabled(true);
            collapsed_title.setVisibility(View.GONE);
        }else{
            toolbar.getMenu().findItem(R.id.search).setVisible(true);
            toolbar.getMenu().findItem(R.id.filter).setVisible(true);
            searchview.setVisibility(View.GONE);
            searchinput.setEnabled(false);
            collapsed_title.setVisibility(View.VISIBLE);
        }
    }

    public void settilte(String title){
        TextView expanded_title = findViewById(R.id.expanded_title);
        TextView collapsed_title = findViewById(R.id.collapsed_title);
        EditText searchinput = findViewById(R.id.searchinput);
        expanded_title.setText(title);
        collapsed_title.setText(title);
        searchinput.setHint("Search "+title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch(item.getItemId()){
            case R.id.search: setsearching(true) ;return true;
            case R.id.filter: Toast.makeText(ScrollingActivity.this, "Filter", Toast.LENGTH_SHORT).show(); return true;
            case R.id.settings: intent.setClass(getApplicationContext(), SettingsActivity.class); startActivity(intent); return true;
            case R.id.about: intent.setClass(getApplicationContext(), AppInfoActivity.class); startActivity(intent); return true;
            case R.id.info: intent.setClass(getApplicationContext(), VideoInfoActivity.class); startActivity(intent); return true;
        }

        return super.onOptionsItemSelected(item);
    }

}