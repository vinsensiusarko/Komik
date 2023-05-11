package arko.komik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    LinearLayout parentPanel;
    MediaPlayer player;
    boolean doublePressedBackExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adjustFontScale(getResources().getConfiguration());

        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("e-Comic");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        parentPanel = (LinearLayout) findViewById(R.id.layoutPanel);
        webView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.getSettings().setTextZoom(100);
        webView.loadUrl("file:///android_asset/index.html");

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (doublePressedBackExit) {

                Intent exitIntent = new Intent(getApplicationContext(), ExitActivity.class);
                startActivity(exitIntent);
                finish();
                return;
            }
            this.doublePressedBackExit = true;
            Snackbar.make(parentPanel, "Tekan lagi untuk keluar...", Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    doublePressedBackExit = false;
                }
            }, 2000);
        }
    }

    public void play() {

        if (player == null) {

            player = MediaPlayer.create(this, R.raw.background_music);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {

                    stopPlayer();
                    player.setLooping(true);
                }
            });
        }

        player.start();
    }

    public void pause() {

        if (player != null) {

            player.pause();
        }
    }

    public void stop() {

        stopPlayer();
    }

    private void stopPlayer() {

        if (player != null) {

            player.release();
            player = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        play();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.on_music) {

            play();
            Toast.makeText(this, "Musik On", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.off_music) {

            stop();
            Toast.makeText(this, "Musik Off", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.exit) {

            SendUserToExitActivity();
        }
        return false;
    }

    private void SendUserToExitActivity() {

        Intent exitIntent = new Intent(getApplicationContext(), ExitActivity.class);
        startActivity(exitIntent);
        finish();
    }

    public void adjustFontScale(Configuration configuration) {

        if (configuration != null) {
            Log.d("TAG", "adjustDisplayScale: " + configuration.densityDpi);
            if (configuration.densityDpi >= 485) //for 6 inch device OR for 538 ppi
                configuration.densityDpi = 500; //decrease "display size" by ~30
            else if (configuration.densityDpi >= 300) //for 5.5 inch device OR for 432 ppi
                configuration.densityDpi = 400; //decrease "display size" by ~30
            else if (configuration.densityDpi >= 100) //for 4 inch device OR for 233 ppi
                configuration.densityDpi = 200; //decrease "display size" by ~30
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.densityDpi * metrics.density;
            this.getResources().updateConfiguration(configuration, metrics);
        }
    }
}