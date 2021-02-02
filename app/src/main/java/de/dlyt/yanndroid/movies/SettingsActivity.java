package de.dlyt.yanndroid.movies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();
        initSettings();

    }

    public void initSettings() {
        sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);

        /** Default Tab */

        TabLayout defaulttab = findViewById(R.id.defaulttab);
        int[] tabicons = {R.drawable.ic_movie, R.drawable.ic_serie, R.drawable.ic_bookmarks};
        for (int i = 0; i < 3; i++) {
            defaulttab.addTab(defaulttab.newTab().setIcon(tabicons[i]));
        }
        defaulttab.selectTab(defaulttab.getTabAt(sharedPreferences.getInt("default_tab", 0)));
        defaulttab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                sharedPreferences.edit().putInt("default_tab", tab.getPosition()).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        /** language_spinner */
        ArrayList<String> language_options = new ArrayList<>();
        language_options.add(getString(R.string.system));
        language_options.add(getString(R.string.english));
        language_options.add(getString(R.string.german));
        language_options.add(getString(R.string.french));

        Spinner language_spinner = findViewById(R.id.language_spinner);
        language_spinner.setAdapter(new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_layout, language_options));
        ((ArrayAdapter) language_spinner.getAdapter()).notifyDataSetChanged();

        language_spinner.setSelection(sharedPreferences.getInt("language_spinner", 0));
        final int[] language_spinner_selection = {sharedPreferences.getInt("language_spinner", 0)};

        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (language_spinner_selection[0] != position) {
                    restartapp();
                }
                sharedPreferences.edit().putInt("language_spinner", position).commit();
                language_spinner_selection[0] = position;
                switch (position) {
                    case 0:
                        setLocale(SettingsActivity.this, Locale.getDefault().getLanguage());
                        return;
                    case 1:
                        setLocale(SettingsActivity.this, "en");
                        return;
                    case 2:
                        setLocale(SettingsActivity.this, "de");
                        return;
                    case 3:
                        setLocale(SettingsActivity.this, "fr");
                        return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /** Theme_spinner */
        ArrayList<String> theme_options = new ArrayList<>();
        theme_options.add(getString(R.string.system));
        theme_options.add(getString(R.string.always));
        theme_options.add(getString(R.string.never));

        Spinner theme_spinner = findViewById(R.id.theme_spinner);
        theme_spinner.setAdapter(new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_layout, theme_options));
        ((ArrayAdapter) theme_spinner.getAdapter()).notifyDataSetChanged();

        theme_spinner.setSelection(sharedPreferences.getInt("theme_spinner", 0));
        final int[] theme_spinner_selection = {sharedPreferences.getInt("theme_spinner", 0)};

        theme_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedPreferences.edit().putInt("theme_spinner", position).commit();
                theme_spinner_selection[0] = position;
                switch (position) {
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /** switches */

        SwitchMaterial switch_mobiledata = findViewById(R.id.switch_mobiledata);
        switch_mobiledata.setChecked(sharedPreferences.getBoolean("switch_mobiledata", true));
        switch_mobiledata.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("switch_mobiledata", isChecked).commit();
            }
        });

        SwitchMaterial switch_wifi = findViewById(R.id.switch_wifi);
        switch_wifi.setChecked(sharedPreferences.getBoolean("switch_wifi", false));
        switch_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("switch_wifi", isChecked).commit();
            }
        });

        SwitchMaterial switch_localserver = findViewById(R.id.switch_localserver);
        LinearLayout localserver_settings = findViewById(R.id.localserver_settings);
        setVisible(localserver_settings, sharedPreferences.getBoolean("switch_localserver", false));
        switch_localserver.setChecked(sharedPreferences.getBoolean("switch_localserver", false));
        switch_localserver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("switch_localserver", isChecked).commit();
                setVisible(localserver_settings, isChecked);
            }
        });

    }


    public void setVisible(View view, Boolean visible) {
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
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

    public void restartapp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        /*RestartDialog bottomSheetDialog = RestartDialog.newInstance();
        bottomSheetDialog.show(getSupportFragmentManager(), "tag");*/
    }

}