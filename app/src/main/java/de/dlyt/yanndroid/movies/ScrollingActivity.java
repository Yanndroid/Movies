
/**
 * todo:
 * filter
 * search
 * bookmark
 * open apk after update
 * animation
 */


package de.dlyt.yanndroid.movies;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrollingActivity extends AppCompatActivity {

    public static int current_tab;
    public String[] tabnames;
    public static TextView expanded_subtitle;
    private DatabaseReference mDatabase;
    private ArrayList<HashMap<String, Object>> updateinfo;

    private boolean searching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        tabnames = new String[]{getString(R.string.movies), getString(R.string.series), getString(R.string.favorites)};
        settilte(getString(R.string.movies));

        checkforupdate();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1000);
        }

        /** Def */
        expanded_subtitle = findViewById(R.id.expanded_subtitle);

        /** collapsing Toolbar */
        initToolbar();

        /** ViewPager */
        initViewPager();

        /** Search */
        searching = false;
        EditText searchinput = findViewById(R.id.searchinput);
        searchinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchinput.setError("Search not available");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void initToolbar() {
        /** Def */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout AppBar = findViewById(R.id.app_bar);

        TextView expanded_title = findViewById(R.id.expanded_title);
        TextView expanded_subtitle = findViewById(R.id.expanded_subtitle);
        TextView collapsed_title = findViewById(R.id.collapsed_title);

        /** 1/3 of the Screen */
        ViewGroup.LayoutParams layoutParams = AppBar.getLayoutParams();
        layoutParams.height = (int) ((double) this.getResources().getDisplayMetrics().heightPixels / 2.4);


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

    public void initViewPager() {
        int[] tabicons = {R.drawable.ic_movie, R.drawable.ic_serie, R.drawable.ic_bookmarks};
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText(tabnames[position]);
                        tab.setIcon(tabicons[position]);
                    }
                }).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                current_tab = tab.getPosition();
                settilte(tabnames[tab.getPosition()]);
                refreshcount();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public void switchsearching() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView collapsed_title = findViewById(R.id.collapsed_title);
        CardView searchview = findViewById(R.id.searchview);
        EditText searchinput = findViewById(R.id.searchinput);

        if (!searching) {

            Snackbar.make(findViewById(R.id.app_bar), "Search currently not available", Snackbar.LENGTH_SHORT).show();


            toolbar.getMenu().findItem(R.id.search).setIcon(R.drawable.ic_close);
            toolbar.getMenu().findItem(R.id.filter).setVisible(false);
            searchview.setVisibility(View.VISIBLE);
            searchinput.setEnabled(true);
            collapsed_title.setVisibility(View.GONE);

            searchview.animate().alpha(1).setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searching = true;
                }
            });

        } else {

            searchview.animate().alpha(0).setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    toolbar.getMenu().findItem(R.id.search).setIcon(R.drawable.ic_search);
                    toolbar.getMenu().findItem(R.id.filter).setVisible(true);
                    searchview.setVisibility(View.GONE);
                    searchinput.setEnabled(false);
                    collapsed_title.setVisibility(View.VISIBLE);
                    searching = false;
                }
            });

        }
    }

    public void settilte(String title) {
        TextView expanded_title = findViewById(R.id.expanded_title);
        TextView collapsed_title = findViewById(R.id.collapsed_title);
        EditText searchinput = findViewById(R.id.searchinput);
        expanded_title.setText(title);
        collapsed_title.setText(title);
        searchinput.setHint(getString(R.string.search) + " " + title);
    }

    public static void refreshcount() {
        expanded_subtitle.setText("" + TabFragment.getlistsize(current_tab));
    }

    public void checkforupdate() {

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionC = pInfo.versionCode;
            String versionN = pInfo.versionName;
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

                    if (versionC < Double.parseDouble(updateinfo.get(0).get("code").toString())) {
                        Snackbar.make(findViewById(R.id.app_bar), getString(R.string.update_available) + ": " + updateinfo.get(0).get("name").toString(), Snackbar.LENGTH_SHORT).setAction(R.string.download, new Snackbarbutton()).show();
                    }
                    if (versionC > Double.parseDouble(updateinfo.get(0).get("code").toString())) {
                        FirebaseDatabase.getInstance().getReference().child("AndroidApp").child("version").child("code").setValue(versionC);
                        FirebaseDatabase.getInstance().getReference().child("AndroidApp").child("version").child("name").setValue(versionN);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public class Snackbarbutton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://github.com/Yanndroid/Movies/raw/master/app/release/app-release.apk");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(getString(R.string.movies_update));
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
            downloadManager.enqueue(request);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
            case R.id.search:
                switchsearching();
                return true;
            case R.id.filter:
                Snackbar.make(findViewById(R.id.app_bar), "Filter currently not available", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.settings:
                intent.setClass(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.about:
                intent.setClass(getApplicationContext(), AppInfoActivity.class);
                startActivity(intent);
                return true;
            case R.id.info:
                intent.setClass(getApplicationContext(), VideoInfoActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}