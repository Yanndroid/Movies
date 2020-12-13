package de.dlyt.yanndroid.movies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Locale;

import de.dlyt.yanndroid.movies.dialogs.RestartDialog;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();
        initSettings();

    }


    public void initSettings() {
        sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);

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
                        setLocale(SettingsActivity.this, "");
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
                if (theme_spinner_selection[0] != position) {
                    restartapp();
                }
                sharedPreferences.edit().putInt("theme_spinner", position).commit();
                theme_spinner_selection[0] = position;
                switch (position) {
                    case 0:
                        return;
                    case 1:
                        return;
                    case 2:
                        return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /** switches */

        SwitchMaterial switch1 = findViewById(R.id.switch1);
        switch1.setChecked(sharedPreferences.getBoolean("switch1", false));
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("switch1", isChecked).commit();
            }
        });

        SwitchMaterial switch2 = findViewById(R.id.switch2);
        switch2.setChecked(sharedPreferences.getBoolean("switch2", false));
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("switch2", isChecked).commit();
            }
        });

        SwitchMaterial switch3 = findViewById(R.id.switch3);
        switch3.setChecked(sharedPreferences.getBoolean("switch3", false));
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("switch3", isChecked).commit();

            }
        });


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

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void restartapp() {
        RestartDialog bottomSheetDialog = RestartDialog.newInstance();
        bottomSheetDialog.show(getSupportFragmentManager(), "tag");
    }

}