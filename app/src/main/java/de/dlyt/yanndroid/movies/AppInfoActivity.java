package de.dlyt.yanndroid.movies;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

import de.dlyt.yanndroid.movies.dialog.UpdateDialog;

import static de.dlyt.yanndroid.movies.R.string.Baguette;
import static de.dlyt.yanndroid.movies.R.string.no_EasterEgg;

public class AppInfoActivity extends AppCompatActivity {

    static Context context;
    BottomSheetDialog bsd;
    Integer clicks;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View displayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        context = getBaseContext();


        /** Popup */
        initPopup();

        CardView yanndroidcard = findViewById(R.id.yanndroidcard);

        clicks = 0;
        yanndroidcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicks++;
                if (clicks == 4) {
                    Snackbar.make(findViewById(R.id.app_bar), no_EasterEgg, Snackbar.LENGTH_LONG).setAction(R.string.ok, new Snackbarbutton()).show();
                    clicks = 0;
                }
            }
        });


        /** App Version */
        TextView expanded_subtitle = findViewById(R.id.expanded_subtitle);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            expanded_subtitle.setText(getString(R.string.version) + ": " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        /** Toolbar */
        initToolbar();

        /** Buttons */
        ImageView ytelegram = findViewById(R.id.ytelegram);
        ImageView ygithub = findViewById(R.id.ygithub);
        ImageView tbrowser = findViewById(R.id.tbrowser);
        ImageView fbrowser = findViewById(R.id.fbrowser);
        ytelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/yanndroid")));
            }
        });
        ygithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Yanndroid/Movies")));
                //Snackbar.make(findViewById(R.id.yanndroidcard),"Source Code isn't public", Snackbar.LENGTH_SHORT).show();
            }
        });
        tbrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/")));
            }
        });
        fbrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebase.google.com/")));
            }
        });


    }

    private void initPopup() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 400;
        layoutParams.height = 400;
        layoutParams.x = 0;
        layoutParams.y = 0;

        _reCall();

    }

    private void showFloatingWindow() {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        displayView = layoutInflater.inflate(R.layout.popup_view, null);
        displayView.setOnTouchListener(new FloatingOnTouchListener());
        ImageView baguette = displayView.findViewById(R.id.baguette);
        objectAnimator.setTarget(baguette);
        objectAnimator.setPropertyName("rotation");
        objectAnimator.setFloatValues((float) (360));
        objectAnimator.setDuration((int) (2000));
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setRepeatCount((int) (-1));
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
        windowManager.addView(displayView, layoutParams);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        baguette.setColorFilter(color);
    }

    public void closes() {
        try {
            windowManager.removeView(displayView);
        } catch (Exception e) {
        }
    }

    private void _reCall() {
    }

    public void initToolbar() {
        /** Def */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout AppBar = findViewById(R.id.app_bar);
        TextView expanded_title = findViewById(R.id.expanded_title);
        TextView title = findViewById(R.id.title);
        TextView expanded_subtitle = findViewById(R.id.expanded_subtitle);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.download) {
            UpdateDialog bottomSheetDialog = UpdateDialog.newInstance(getBaseContext());
            bottomSheetDialog.show(getSupportFragmentManager(), "tag");
        }

        return super.onOptionsItemSelected(item);
    }

    public class Snackbarbutton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (android.provider.Settings.canDrawOverlays(AppInfoActivity.this)) {
                showFloatingWindow();
                Toast.makeText(AppInfoActivity.this, Baguette, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                while (true) {
                    if (android.provider.Settings.canDrawOverlays(AppInfoActivity.this)) {
                        showFloatingWindow();
                        Toast.makeText(AppInfoActivity.this, Baguette, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            ImageView baguette = view.findViewById(R.id.baguette);
            Random rnd;
            int color;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    rnd = new Random();
                    color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    baguette.setColorFilter(color);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    rnd = new Random();
                    color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    baguette.setColorFilter(color);
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}