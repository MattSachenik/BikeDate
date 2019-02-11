package bikedate.org.bikedate;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    private final static long SPLASH_TIME_OUT = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                //animation
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
