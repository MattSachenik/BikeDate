package bikedate.org.bikedate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nihaskalam.progressbuttonlibrary.CircularProgressButton;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //ScreenElements
    //private Button mRegisterBtn;
    private EditText mEmailET, mPasswordET, mReEnterPasswordET, mNameET;
    private CircularProgressButton mRegisterBtnAnim;

    //Database Elements
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseASL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Make sure EditTexts stay down unless clicked
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Connect
        //mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mEmailET = (EditText) findViewById(R.id.emailET);
        mPasswordET = (EditText) findViewById(R.id.passwordET);
        mReEnterPasswordET = (EditText) findViewById(R.id.reEnterPasswordET);
        mNameET = (EditText) findViewById(R.id.nameET);
        mRegisterBtnAnim = findViewById(R.id.registerBtnAnim);
        mRegisterBtnAnim.setIndeterminateProgressMode(true);

        //Authenticate User
        mAuth = FirebaseAuth.getInstance();
        firebaseASL = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    mRegisterBtnAnim.showComplete();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class));
                            finish();
                        }
                    }, 1000);
                }
            }
        };

        mRegisterBtnAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterBtnAnim.showProgress();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final String email = mEmailET.getText().toString();
                        final String password = mPasswordET.getText().toString();
                        final String password2 = mReEnterPasswordET.getText().toString();
                        final String username = mNameET.getText().toString();

                        if (!email.isEmpty() && !password.isEmpty() && !password2.isEmpty() && !username.isEmpty()) {
                            Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(username);

                            usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        //Toast.makeText(RegisterActivity.this, "Sorry! That Username Has Been Taken", Toast.LENGTH_SHORT).show();
                                        mRegisterBtnAnim.setErrorText("Sorry! That Username Has Been Taken");
                                        mRegisterBtnAnim.showError();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mRegisterBtnAnim.showIdle();
                                            }
                                        }, 2500);
                                    }
                                    else {
                                        if (password.equals(password2)) {
                                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (!task.isSuccessful()) {
                                                        //Toast.makeText(RegisterActivity.this, "Sign Up Error", Toast.LENGTH_SHORT).show();
                                                        if (email.contains("@")) {
                                                            mRegisterBtnAnim.setErrorText("Sorry! That Email Has Been Taken");
                                                            mRegisterBtnAnim.showError();
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    mRegisterBtnAnim.showIdle();
                                                                }
                                                            }, 2500);
                                                        } else {
                                                            mRegisterBtnAnim.setErrorText("Invaild Email Address");
                                                            mRegisterBtnAnim.showError();
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    mRegisterBtnAnim.showIdle();
                                                                }
                                                            }, 2500);
                                                        }
                                                    }
                                                    else {
                                                        String userID = mAuth.getCurrentUser().getUid();
                                                        final DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                                                        Map newPost = new HashMap();
                                                        newPost.put("username", username);
                                                        newPost.put("email", email);

                                                        currentUserDb.updateChildren(newPost);
                                                    }
                                                }
                                            });
                                        } else {
                                            //Toast.makeText(RegisterActivity.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                                            mRegisterBtnAnim.setErrorText("Passwords Do Not Match");
                                            mRegisterBtnAnim.showError();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mRegisterBtnAnim.showIdle();
                                                }
                                            }, 2500);

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    //Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    mRegisterBtnAnim.setErrorText("Error");
                                    mRegisterBtnAnim.showError();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mRegisterBtnAnim.showIdle();
                                        }
                                    }, 2500);
                                }
                            });
                        }
                        else {
                            //Toast.makeText(RegisterActivity.this, "Please Fill In All Fields", Toast.LENGTH_SHORT).show();
                            mRegisterBtnAnim.setErrorText("Please Fill In All Fields");
                            mRegisterBtnAnim.showError();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mRegisterBtnAnim.showIdle();
                                }
                            }, 2500);
                        }
                    }
                }, 1500);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
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
}
