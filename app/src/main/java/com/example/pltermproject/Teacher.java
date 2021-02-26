package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class Teacher extends AppCompatActivity {

    TextView name, email, phone, academic_rank, courses_clickable, helloText;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    LinearLayout coursesLayoutButton, assignmentsLayoutButton, announcementsLayoutButton, messagesLayoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        name = findViewById(R.id.fetched_teachername_text);
        email = findViewById(R.id.fetched_teachermail_text);
        phone = findViewById(R.id.fetched_phone_text);
        academic_rank = findViewById(R.id.fetched_ar_text);
        userId = fAuth.getCurrentUser().getUid();
        coursesLayoutButton = findViewById(R.id.courses_layout);
        assignmentsLayoutButton = findViewById(R.id.assignments_layout_teacher);
        announcementsLayoutButton = findViewById(R.id.announcements_layout_teacher);
        messagesLayoutButton = findViewById(R.id.messages_layout_teacher);
        helloText = findViewById(R.id.your_profile_text);

        getSupportActionBar().setTitle("Your Profile");


        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(Teacher.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){

                    String academicRankString = documentSnapshot.get("academic_rank").toString();
                    String phoneString = documentSnapshot.get("phone").toString();
                    String nameString = documentSnapshot.get("name").toString();
                    String emailString = documentSnapshot.get("email").toString();

                    TeacherUser currentTeacher = new TeacherUser(academicRankString, phoneString, nameString, emailString,"teacher");

                    name.setText("Name: " + currentTeacher.getName());
                    email.setText("E-mail: " + currentTeacher.getEmail());
                    phone.setText("Phone: " + currentTeacher.getPhone());
                    academic_rank.setText("Academic Rank: " + currentTeacher.getAcademicRank());
                    helloText.setText("Welcome " + currentTeacher.getName());
                }

            }
        });
        coursesLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), CoursesTeacher.class));

            }
        });
        assignmentsLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), AssignmentsTeacher.class));
            }
        });
        announcementsLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), AnnouncementsTeacher.class));
            }
        });
        messagesLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), InboxList.class);
                intent.putExtra("UserType", "teacher");
                startActivity(intent);
            }
        });
    }
    public void logout (View view) {
        //FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), Login.class));

    }
}
