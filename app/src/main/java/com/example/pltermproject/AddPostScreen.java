package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class AddPostScreen extends AppCompatActivity {

    int[] IMAGES = {R.drawable.avatar_1, R.drawable.avatar_10, R.drawable.avatar_13, R.drawable.avatar_15, R.drawable.avatar_16, R.drawable.avatar_5, R.drawable.avatar_4, R.drawable.avatar_8};
    String courseId, userType, userName;
    TextView postText;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ImageButton uploadPostButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_screen);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        postText = findViewById(R.id.share_post_text);
        uploadPostButton = findViewById(R.id.upload_post_button);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            courseId = bundle.getString("CourseId");
            userType = bundle.getString("UserType");
            userName = bundle.getString("UserName");
        }

        getSupportActionBar().setTitle("Share a Post");

        uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference docRef = fStore.collection("posts").document();
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Date date = Calendar.getInstance().getTime();
                            String postedBy = userName;
                            String postString = postText.getText().toString();
                            if(TextUtils.isEmpty(postString)){
                                postText.setError("Post text is required.");
                                return;
                            }
                            ArrayList comments = new ArrayList();
                            Random rand = new Random();
                            int random = rand.nextInt(IMAGES.length);
                            String image = String.valueOf(random);
                            Post post = new Post(date, postedBy, postString, comments, courseId, image);

                            docRef.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddPostScreen.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                                        final Intent intent2 = new Intent(AddPostScreen.this, Stream.class);
                                        intent2.putExtra("UserType", userType);
                                        intent2.putExtra("CourseId", courseId);
                                        finish();
                                        startActivity(intent2);
                                        return;
                                    } else {
                                        Toast.makeText(AddPostScreen.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });


    }

    public boolean onSupportNavigateUp(){

            final Intent intent2 = new Intent(AddPostScreen.this, Stream.class);
            intent2.putExtra("UserType",userType);
            intent2.putExtra("CourseId",courseId);
            finish();
            startActivity(intent2);
            return true;
    }
}
