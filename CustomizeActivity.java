package bikedate.org.bikedate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomizeActivity extends AppCompatActivity{

    //Strings
    private String userId, name, image1Url, image2Url, image3Url, image4Url, biketype, description;

    //Screen Elements
    private EditText mActualNameET, mDescriptionET;
    private ImageView mImage1, mImage2, mImage3, mImage4;
    private Button mBackToOptionsBtn, mViewProfileBtn;
    private Spinner dropdown;
    com.shawnlin.numberpicker.NumberPicker mNumberPicker;

    //Database Elements
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDb;

    //Array Adapter
    ArrayAdapter<String> adapter;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        //Make sure EditTexts stay down unless clicked
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Connect Screen Elements
        mActualNameET = findViewById(R.id.actualNameET);
        mDescriptionET = findViewById(R.id.descriptionET);
        mImage1 = findViewById(R.id.image1);
        mImage2 = findViewById(R.id.image2);
        mImage3 = findViewById(R.id.image3);
        mImage4 = findViewById(R.id.image4);
        mBackToOptionsBtn = findViewById(R.id.backToOptionsBtn);
        mViewProfileBtn = findViewById(R.id.viewProfileBtn);
        dropdown = findViewById(R.id.bikeTypeSpinner);
        mNumberPicker = findViewById(R.id.numberPicker);

        //Connect Database Elements
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        String[] items = new String[]{"Bike Type (None)", "Cruiser", "Electric Bicycle", "Road", "Mountain", "Single Speed"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        
        getUserInfo();

        mImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        mImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });

        mImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 4);
            }
        });

        //Buttons
        mBackToOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mViewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                Intent intent = new Intent(CustomizeActivity.this, RiderActivity.class);
                Bundle b = new Bundle();
                b.putString("riderId", userId);
                b.putBoolean("isFriend", false);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    private void saveUserInfo() {
        biketype = dropdown.getSelectedItem().toString();
        switch (biketype) {
            case "Bike Type (None)":
                biketype = "none";
                break;
            case "Cruiser":
                biketype = "cruiser";
                break;
            case "Electric Bicycle":
                biketype = "electricBicycle";
                break;
            case "Road":
                biketype = "master";
                break;
            case "Mountain":
                biketype = "mountain";
                break;
            case "Single Speed":
                biketype = "singleSpeed";
                break;
        }
        Map userInfo = new HashMap();
        userInfo.put("age", mNumberPicker.getValue());
        userInfo.put("name", mActualNameET.getText().toString());
        userInfo.put("description", mDescriptionET.getText().toString());
        userInfo.put("biketype", biketype);
        mUserDb.updateChildren(userInfo);
    }

    private void saveUserPicture(final int x) {
        if(resultUri != null){
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CustomizeActivity.this, "Error: Couldn't Upload Image", Toast.LENGTH_SHORT).show();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map userInfo = new HashMap();
                        userInfo.put("image"+x+"Url", uri.toString());
                        mUserDb.updateChildren(userInfo);
                    }
                });
                }
            });
        }
        else{
            Toast.makeText(CustomizeActivity.this, "No Picture Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void getUserInfo() {
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    //Glide.with(CustomizeActivity.this).clear(mImage1);
                    Glide.with(CustomizeActivity.this).clear(mImage1);
                    if (map.get("image1Url") != null) {
                        image1Url = map.get("image1Url").toString();
                        switch(image1Url){
                            case "default":
                                Glide.with(CustomizeActivity.this).load(R.drawable.add_item).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage1);
                                break;
                            default:
                                Glide.with(CustomizeActivity.this).load(image1Url).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage1);
                                break;
                        }
                    }
                    if (map.get("image2Url") != null) {
                        image2Url = map.get("image2Url").toString();
                        switch(image2Url){
                            case "default":
                                Glide.with(CustomizeActivity.this).load(R.drawable.add_item).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage2);
                                break;
                            default:
                                Glide.with(CustomizeActivity.this).load(image2Url).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage2);
                                break;
                        }
                    }
                    if (map.get("image3Url") != null) {
                        image3Url = map.get("image3Url").toString();
                        switch(image3Url){
                            case "default":
                                Glide.with(CustomizeActivity.this).load(R.drawable.add_item).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage3);
                                break;
                            default:
                                Glide.with(CustomizeActivity.this).load(image3Url).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage3);
                                break;
                        }
                    }
                    if (map.get("image4Url") != null) {
                        image4Url = map.get("image4Url").toString();
                        switch(image4Url){
                            case "default":
                                Glide.with(CustomizeActivity.this).load(R.drawable.add_item).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage4);
                                break;
                            default:
                                Glide.with(CustomizeActivity.this).load(image4Url).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage4);
                                break;
                        }
                    }
                    if (map.get("age") != null) {
                        int age =  dataSnapshot.child("age").getValue(Integer.class);
                        mNumberPicker.setValue(age);
                    }
                    if (map.get("biketype") != null) {
                        biketype = map.get("biketype").toString();
                        if (biketype != null) {
                            switch (biketype) {
                                case "none":
                                    biketype = "Bike Type (None)";
                                    break;
                                case "cruiser":
                                    biketype = "Cruiser";
                                    break;
                                case "electricBicycle":
                                    biketype = "Electric Bicycle";
                                    break;
                                case "master":
                                    biketype = "Road";
                                    break;
                                case "mountain":
                                    biketype = "Mountain";
                                    break;
                                case "singleSpeed":
                                    biketype = "Single Speed";
                                    break;
                            }
                            int spinnerPosition = adapter.getPosition(biketype);
                            dropdown.setSelection(spinnerPosition);
                        }
                    }
                    if (map.get("description") != null) {
                        description =  map.get("description").toString();
                        mDescriptionET.setText(description);
                    }
                    if (map.get("name") != null) {
                        String name =  map.get("name").toString();
                        mActualNameET.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomizeActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            resultUri = data.getData();
            //Glide.with(CustomizeActivity.this).clear(mImage1);
            Glide.with(CustomizeActivity.this).load(resultUri).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage1);
            saveUserPicture(1);
        }
        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            resultUri = data.getData();
            //Glide.with(CustomizeActivity.this).clear(mImage2);
            Glide.with(CustomizeActivity.this).load(resultUri).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage2);
            saveUserPicture(2);
        }
        if(requestCode == 3 && resultCode == Activity.RESULT_OK){
            resultUri = data.getData();
            //Glide.with(CustomizeActivity.this).clear(mImage3);
            Glide.with(CustomizeActivity.this).load(resultUri).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage3);
            saveUserPicture(3);
        }
        if(requestCode == 4 && resultCode == Activity.RESULT_OK){
            resultUri = data.getData();
            //Glide.with(CustomizeActivity.this).clear(mImage4);
            Glide.with(CustomizeActivity.this).load(resultUri).apply(RequestOptions.circleCropTransform()).transition(DrawableTransitionOptions.withCrossFade()).into(mImage4);
            saveUserPicture(4);
        }
    }

    @Override
    public void onBackPressed()
    {
        saveUserInfo();
        super.onBackPressed();
    }

}
