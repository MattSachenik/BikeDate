package bikedate.org.bikedate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //Screen Elements
    private Button mSignUpBtn, mLoginBtn, mResetBtn;
    public Button mBackBtn;
    private EditText mLoginEmail, mLoginPassword, mResetET;
    private CheckBox mRememberMeCB;
    private TextView mForgetPasswordTV;
    public LinearLayout mResetPasswordLayer;

    //Database Elements
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseASL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Make sure EditTexts stay down unless clicked
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Connect
        mSignUpBtn = findViewById(R.id.signUpBtn);
        mLoginBtn = findViewById(R.id.loginBtn);
        mBackBtn = findViewById(R.id.backBtn);
        mResetBtn = findViewById(R.id.resetBtn);
        mLoginEmail = findViewById(R.id.loginEmailET);
        mLoginPassword = findViewById(R.id.loginPasswordET);
        mResetET = findViewById(R.id.resetET);
        mRememberMeCB = findViewById(R.id.rememberMeCB);
        mForgetPasswordTV = findViewById(R.id.forgetPasswordTV);
        mResetPasswordLayer = findViewById(R.id.fadeInResetLL);

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        //Authenticate User
        mAuth = FirebaseAuth.getInstance();
        firebaseASL = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    startActivity(new Intent(LoginActivity.this, TabActivity.class));
                    finish();
                }
            }
        };

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mLoginEmail.getText().toString();
                final String password = mLoginPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Email and Password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                //Toast.makeText(LoginActivity.this, "Sign In Error", Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, ((FirebaseApiNotAvailableException) task.getException()).getMessage() , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //Reset Password Module
        final Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { mForgetPasswordTV.setClickable(false); }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mResetPasswordLayer.setVisibility(View.VISIBLE);
                mLoginBtn.setClickable(false);
                mSignUpBtn.setClickable(false);
                mLoginEmail.setClickable(false);
                mLoginPassword.setClickable(false);
                mSignUpBtn.setClickable(false);
            }
        });

        final Animation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mResetPasswordLayer.setVisibility(View.GONE);
                mLoginBtn.setClickable(true);
                mSignUpBtn.setClickable(true);
                mLoginEmail.setClickable(true);
                mLoginPassword.setClickable(true);
                mSignUpBtn.setClickable(true);
                mForgetPasswordTV.setClickable(true);
            }
        });

        mForgetPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetPasswordLayer.startAnimation(fadeIn);
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetPasswordLayer.startAnimation(fadeOut);
            }
        });

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailReset = mResetET.getText().toString();
                if (emailReset.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Please Enter An Email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailReset)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseASL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseASL);
    }

    @Override
    public void onBackPressed()
    {
        final Animation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mResetPasswordLayer.setVisibility(View.GONE);
                mLoginBtn.setClickable(true);
                mSignUpBtn.setClickable(true);
                mLoginEmail.setClickable(true);
                mLoginPassword.setClickable(true);
                mSignUpBtn.setClickable(true);
                mForgetPasswordTV.setClickable(true);
            }
        });

        mResetPasswordLayer.startAnimation(fadeOut);
    }
}
