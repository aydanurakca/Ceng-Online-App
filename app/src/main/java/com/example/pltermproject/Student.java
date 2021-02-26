package com.example.pltermproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class Student extends AppCompatActivity {

    TextView name, email, student_number, GPA, grade, helloText;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    LinearLayout coursesLayoutButton, assignmentsLayoutButton, announcementsLayoutButton, messagesLayoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        name = findViewById(R.id.fetched_studentname_text);
        email = findViewById(R.id.fetched_studentmail_text);
        student_number = findViewById(R.id.fetched_student_number_text);
        GPA = findViewById(R.id.fetched_gpa_text);
        grade = findViewById(R.id.fetched_grade_text);
        userId = fAuth.getCurrentUser().getUid();
        coursesLayoutButton = findViewById(R.id.courses_layout);
        assignmentsLayoutButton = findViewById(R.id.assignments_layout_student);
        announcementsLayoutButton = findViewById(R.id.announcements_layout_student);
        messagesLayoutButton = findViewById(R.id.messages_layout_student);
        helloText = findViewById(R.id.your_profile_text);


        getSupportActionBar().setTitle("Your Profile");
        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(Student.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String gpaString = documentSnapshot.get("GPA").toString();
                    String studentNumberString = documentSnapshot.get("student_number").toString();
                    String gradeString = documentSnapshot.get("grade").toString();
                    String nameString = documentSnapshot.get("name").toString();
                    String emailString = documentSnapshot.get("email").toString();

                    StudentUser currentStudent = new StudentUser(gpaString, studentNumberString, gradeString, nameString, emailString, "student");

                    name.setText("Name: " + currentStudent.getName());
                    student_number.setText("Student Number: " + currentStudent.getStudentNumber());
                    email.setText("E-mail: " + currentStudent.getEmail());
                    GPA.setText("GPA: " + currentStudent.getGPA());
                    grade.setText("Grade: " + currentStudent.getGrade());
                    helloText.setText("Welcome " + currentStudent.getName());
                }

            }
        });
        coursesLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), CoursesStudent.class));

            }
        });
        assignmentsLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), AssignmentsStudent.class));
            }
        });
        announcementsLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), AnnouncementsStudent.class));
            }
        });
        messagesLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), InboxList.class);
                intent.putExtra("UserType", "student");
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
