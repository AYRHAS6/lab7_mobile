package projet.ensa.ma.stars.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import projet.ensa.ma.stars.R;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);

        logo.setAlpha(0f);
        logo.setScaleX(0.3f);
        logo.setScaleY(0.3f);

        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotation(360f)
                .setDuration(1000)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(() -> {
                    logo.animate()
                            .translationYBy(300f)
                            .scaleX(0.5f)
                            .scaleY(0.5f)
                            .alpha(0f)
                            .setDuration(800)
                            .setStartDelay(600)
                            .withEndAction(this::goToMain)
                            .start();
                })
                .start();
    }

    private void goToMain() {
        startActivity(new Intent(SplashActivity.this, ListActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logo != null) logo.animate().cancel();
    }
}
