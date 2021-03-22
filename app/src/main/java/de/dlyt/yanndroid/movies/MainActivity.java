package de.dlyt.yanndroid.movies;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.Locale;
import java.util.Timer;

import de.dlyt.yanndroid.movies.adapter.ViewPagerAdapter;
import de.dlyt.yanndroid.movies.utilities.ItemViewModel;

public class MainActivity extends AppCompatActivity {

    public static TextView expanded_subtitle;
    public static Boolean datasaving;
    public String[] tabnames;
    private DatabaseReference mDatabase;
    private ArrayList<HashMap<String, Object>> updateinfo;
    private boolean searching = false;
    private boolean filtering = false;
    private ItemViewModel viewModel;
    private HashMap<String, String> newFilter = new HashMap<>();

    public static void refreshcount(Integer current_tab) {
        expanded_subtitle.setText("" + TabFragment.getlistsize(current_tab));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLanguageAndTheme();

        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        tabnames = new String[]{getString(R.string.movies), getString(R.string.series), getString(R.string.favorites)};
        settilte(getString(R.string.movies));

        checkforconnection();
        checkforupdate();

        /** permissions */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }


        /** Def */
        expanded_subtitle = findViewById(R.id.expanded_subtitle);

        /** collapsing Toolbar */
        initToolbar();
        initDrawer();

        /** ViewPager */
        initViewPager();

        /** Search and Filter */
        newFilter.put("search", "");
        newFilter.put("language", "");
        newFilter.put("resolution", "");

        EditText searchinput = findViewById(R.id.searchinput);
        searchinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //searchinput.setError(getString(R.string.Search_not_available));
                applyFilter("search", s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayList<String> language_options = new ArrayList<>();
        language_options.add(getString(R.string.all));
        language_options.add(getString(R.string.english));
        language_options.add(getString(R.string.german));
        language_options.add(getString(R.string.french));
        language_options.add(getString(R.string.russian));
        String[] languages = {"", "English", "Deutsch", "Français", "Pусский"};

        Spinner language_filter = findViewById(R.id.language_filter);
        language_filter.setAdapter(new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_layout, language_options));
        ((ArrayAdapter) language_filter.getAdapter()).notifyDataSetChanged();
        language_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (filtering) {
                    applyFilter("language", languages[position].toLowerCase());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayList<String> resolution_options = new ArrayList<>();
        resolution_options.add(getString(R.string.all));
        resolution_options.add("FHD");
        resolution_options.add("HD");
        String[] resolutions = {"", "1080p", "720p"};


        Spinner resolution_filter = findViewById(R.id.resolution_filter);
        resolution_filter.setAdapter(new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_layout, resolution_options));
        ((ArrayAdapter) resolution_filter.getAdapter()).notifyDataSetChanged();
        resolution_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (filtering) {
                    applyFilter("resolution", resolutions[position].toLowerCase());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void applyFilter(String key, String value) {
        newFilter.put(key, value);
        viewModel.setHashMap(newFilter);
    }


    public void setLanguageAndTheme() {

        SharedPreferences sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);

        switch (sharedPreferences.getInt("language_spinner", 0)) {
            case 0:
                setLocale(MainActivity.this, Locale.getDefault().getLanguage());
                return;
            case 1:
                setLocale(MainActivity.this, "en");
                return;
            case 2:
                setLocale(MainActivity.this, "de");
                return;
            case 3:
                setLocale(MainActivity.this, "fr");
                return;
        }

        switch (sharedPreferences.getInt("theme_spinner", 0)) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                return;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return;
        }

    }

    public void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void checkforconnection() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        AppBarLayout AppBar = findViewById(R.id.app_bar);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
            alert(getString(R.string.mobile_data), R.color.yellow);
            datasaving = sharedPreferences.getBoolean("switch_mobiledata", true);
        } else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            alert(getString(R.string.wifi), R.color.green);
            datasaving = sharedPreferences.getBoolean("switch_wifi", false);
        } else {
            alert(getString(R.string.offline), R.color.red);
        }
    }


    public void initDrawer() {
        View content = findViewById(R.id.main_content);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        View drawer = findViewById(R.id.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        ViewGroup.LayoutParams layoutParams = drawer.getLayoutParams();
        layoutParams.width = (int) ((double) this.getResources().getDisplayMetrics().widthPixels / 1.19);
        drawerLayout.setScrimColor(ContextCompat.getColor(getBaseContext(), R.color.drawer_dim_color));
        drawerLayout.setDrawerElevation(0);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.opend, R.string.closed) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawer, true);
            }
        });


        View drawer_settings = findViewById(R.id.drawer_settings);
        View drawer_appinfo = findViewById(R.id.drawer_appinfo);
        View drawer_videoinfo = findViewById(R.id.drawer_videoinfo);

        drawer_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        drawer_appinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AppInfoActivity.class);
                startActivity(intent);
            }
        });
        drawer_videoinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), VideoInfoActivity.class);
                startActivity(intent);
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
        layoutParams.height = (int) ((double) this.getResources().getDisplayMetrics().heightPixels / 2.6);


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
                settilte(tabnames[tab.getPosition()]);
                refreshcount(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        tabLayout.selectTab(tabLayout.getTabAt(sharedPreferences.getInt("default_tab", 0)));
    }

    public void switchsearching() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView collapsed_title = findViewById(R.id.collapsed_title);
        CardView searchview = findViewById(R.id.searchview);
        EditText searchinput = findViewById(R.id.searchinput);

        if (!searching) {
            /** Search */
            //Snackbar.make(findViewById(R.id.app_bar), Search_currently_not_available, Snackbar.LENGTH_SHORT).show();

            AppBarLayout AppBar = findViewById(R.id.app_bar);
            AppBar.setExpanded(false, true);

            toolbar.getMenu().findItem(R.id.search).setIcon(R.drawable.ic_close);
            toolbar.getMenu().findItem(R.id.filter).setVisible(false);
            searchview.setVisibility(View.VISIBLE);
            searchinput.setEnabled(true);
            collapsed_title.setVisibility(View.GONE);

            searchview.animate().alpha(1).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searching = true;
                }
            });

        } else {
            /** not Search */
            searchinput.setText("");
            searchview.animate().alpha(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
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

    public void switchfiltering() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView collapsed_title = findViewById(R.id.collapsed_title);
        CardView filterview = findViewById(R.id.filterview);
        Spinner language_filter = findViewById(R.id.language_filter);
        Spinner resolution_filter = findViewById(R.id.resolution_filter);

        if (!filtering) {
            /** Filter */

            AppBarLayout AppBar = findViewById(R.id.app_bar);
            AppBar.setExpanded(false, true);

            toolbar.getMenu().findItem(R.id.filter).setIcon(R.drawable.ic_close);
            toolbar.getMenu().findItem(R.id.search).setVisible(false);
            filterview.setVisibility(View.VISIBLE);
            language_filter.setEnabled(true);
            resolution_filter.setEnabled(true);
            collapsed_title.setVisibility(View.GONE);

            filterview.animate().alpha(1).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    filtering = true;
                }
            });

        } else {
            /** not Filter */
            language_filter.setSelection(0);
            resolution_filter.setSelection(0);
            filterview.animate().alpha(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    toolbar.getMenu().findItem(R.id.filter).setIcon(R.drawable.ic_filter);
                    toolbar.getMenu().findItem(R.id.search).setVisible(true);
                    filterview.setVisibility(View.GONE);
                    language_filter.setEnabled(false);
                    resolution_filter.setEnabled(false);
                    collapsed_title.setVisibility(View.VISIBLE);
                    filtering = false;
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
                        Snackbar.make(findViewById(R.id.app_bar), getString(R.string.update_available) + ": " + updateinfo.get(0).get("name").toString(), Snackbar.LENGTH_LONG).setAction(R.string.download, new Snackbarbutton()).show();
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
                switchfiltering();
                return true;
            /*case R.id.settings:
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
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    public void alert(String text, Integer color) {
        View alertBar = findViewById(R.id.alertBar);
        TextView alertBarText = findViewById(R.id.alertBarText);
        Timer timer;
        timer = new Timer();
        alertBar.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), color));
        alertBarText.setText(text);

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(2000);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                alertBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                alertBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        alertBar.startAnimation(anim);

    }

    public class Snackbarbutton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:de.dlyt.yanndroid.movies")));
            }
            UpdateApp.DownloadAndInstall(getBaseContext(), "https://github.com/Yanndroid/Movies/raw/master/app/release/app-release.apk", "Movies_" + updateinfo.get(0).get("name").toString() + ".apk", "Movies Update", updateinfo.get(0).get("name").toString());
        }
    }


}