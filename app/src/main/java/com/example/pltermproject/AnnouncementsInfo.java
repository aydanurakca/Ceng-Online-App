package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class AnnouncementsInfo extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView announcementSubjectText, announcementDescriptionBox, announcementSubjectFetched, announcementDateFetched, announcementSenderFetched, announcementDescriptionFetched, announcementCourseFetched, descriptionText, announcementDescriptionText;
    Spinner spinner;
    String userId, userType, selectedItem, courseName, announcementID, savedBy, announcementIDBundle;
    ImageButton addAnnouncementButton, saveEditButton;
    ArrayAdapter arrayAdapter;
    ArrayList<String> lectures = new ArrayList<>();
    ArrayList<String> courses = new ArrayList<>();
    ArrayList<String> temp = new ArrayList<>();
    ArrayList<String> announcementsArrayList = new ArrayList<>();
    String [] tokens, tokens2;
    final String ACTIONBARSTRING = "Announcement Info";
    Button editButton, deleteButton;
    public int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements_info);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        announcementSubjectText = findViewById(R.id.announcement_subject_edit_text);
        announcementDescriptionBox = findViewById(R.id.announcement_description_edit_box);
        spinner = findViewById(R.id.courses_spinner);
        addAnnouncementButton = findViewById(R.id.add_announcement_button);
        announcementSubjectFetched = findViewById(R.id.announcement_subject_text_placeholder);
        announcementDateFetched = findViewById(R.id.announcement_date_text_placeholder);
        announcementSenderFetched = findViewById(R.id.announcement_sender_text_placeholder);
        announcementDescriptionFetched = findViewById(R.id.announcement_description_text_box);
        announcementCourseFetched = findViewById(R.id.announcement_course_text_placeholder);
        editButton = findViewById(R.id.announcement_edit_button);
        deleteButton = findViewById(R.id.delete_announcement_button);
        descriptionText = findViewById(R.id.announcement_description_text2);
        announcementDescriptionText = findViewById(R.id.announcement_description_text);
        saveEditButton = findViewById(R.id.save_edit_button);

        if (fAuth.getCurrentUser() != null) {
            userId = fAuth.getCurrentUser().getUid();
        }
        getSupportActionBar().setTitle(ACTIONBARSTRING);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userType = bundle.getString("UserType");
            announcementIDBundle = bundle.getString("announcementID");
        }
        if(announcementIDBundle.equals("0000")){
            makeEditVisible();
            DocumentReference docRef = fStore.collection("users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        savedBy = document.get("name").toString();
                        if(document.exists()){
                            lectures = (ArrayList) document.get("lectures");
                            for (int i = 0; i < lectures.size(); i++) {
                                DocumentReference docRef = fStore.collection("courses").document(lectures.get(i));
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if(document.exists()){
                                                courseName = document.get("code").toString() + " - "  + document.get("name");
                                                courses.add(courseName);
                                                arrayAdapter = new ArrayAdapter(AnnouncementsInfo.this, android.R.layout.simple_spinner_item, courses);
                                                spinner.setAdapter(arrayAdapter);
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }
                }
            });
        }
        else{
            DocumentReference documentReference = fStore.collection("announcements").document(announcementIDBundle);
            documentReference.addSnapshotListener(AnnouncementsInfo.this, new EventListener<DocumentSnapshot>() {

                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        announcementSubjectFetched.setText(documentSnapshot.get("subject").toString());
                        announcementDateFetched.setText("Modified Date: " + documentSnapshot.get("modifiedDate").toString());
                        announcementDescriptionFetched.setText(documentSnapshot.get("description").toString());
                        announcementSenderFetched.setText("Saved By: " + documentSnapshot.get("savedBy").toString());
                        announcementCourseFetched.setText("Course: " + documentSnapshot.get("course").toString());
                        announcementDescriptionBox.setText(documentSnapshot.get("description").toString());
                        announcementSubjectText.setText(documentSnapshot.get("subject").toString());

                    }
                }
            });

        }

        if(userType.equals("student")){
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);

        }
        else if(userType.equals("teacher") && !(announcementIDBundle.equals("0000"))){
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }
        addAnnouncementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAnnouncement();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditVisible();
                spinner.setVisibility(View.GONE);
                saveEditButton.setVisibility(View.VISIBLE);
            }
        });
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAnnouncement();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnnouncement();
            }
        });

    }
    public boolean onSupportNavigateUp(){
        if (userType.equals("teacher")) {
            final Intent intent2 = new Intent(AnnouncementsInfo.this,AnnouncementsTeacher.class);
            finish();
            startActivity(intent2);
        }else if (userType.equals("student")) {
            final Intent intent2 = new Intent(AnnouncementsInfo.this,AnnouncementsStudent.class);
            finish();
            startActivity(intent2);
        }

        return true;
    }
    public void makeEditVisible() {
        announcementSubjectText.setVisibility(View.VISIBLE);
        announcementDescriptionBox.setVisibility(View.VISIBLE);
        announcementDescriptionText.setVisibility(View.VISIBLE);
        addAnnouncementButton.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);


        announcementCourseFetched.setVisibility(View.GONE);
        announcementDescriptionFetched.setVisibility(View.GONE);
        announcementSenderFetched.setVisibility(View.GONE);
        announcementDateFetched.setVisibility(View.GONE);
        announcementSubjectFetched.setVisibility(View.GONE);
        descriptionText.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

    }
    public void addAnnouncement(){
        final String subject = announcementSubjectText.getText().toString().trim();
        if(TextUtils.isEmpty(subject)){
            announcementSubjectText.setError("Announcement Subject is required!");
            return;
        }
        String description = announcementDescriptionBox.getText().toString().trim();

        selectedItem = spinner.getSelectedItem().toString();
        tokens = selectedItem.split("-");
        tokens[0] = tokens[0].trim();
        announcementID = tokens[0] + "Announcement" + (count); // CME3030Announcement1
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(currentTime);
        final Announcement announcement = new Announcement(subject, savedBy, date, tokens[0], description);
        final DocumentReference dbCourses = fStore.collection("courses").document(tokens[0]);
        final DocumentReference dbAnnouncements = fStore.collection("announcements").document(announcementID);
        final Map<String, Object> announcements = new HashMap<>();
        dbCourses.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        temp = (ArrayList) document.get("announcements");
                        if(temp != null){
                            announcementsArrayList.addAll(temp);
                        }
                        if(!(temp.contains(announcementID))){
                            announcementsArrayList.add(announcementID);
                            announcements.put("announcements", announcementsArrayList);
                            dbCourses.update(announcements);
                            dbAnnouncements.set(announcement).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AnnouncementsInfo.this, "Announcement Added", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AnnouncementsInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            int counter = Integer.parseInt(announcementID.substring(announcementID.length() - 1));
                            while (announcementsArrayList.contains(announcementID)){
                                counter++;
                                announcementID = announcementID.substring(0,announcementID.length() - 1) + counter;
                            }
                            announcementsArrayList.add(announcementID);
                            announcements.put("announcements", announcementsArrayList);
                            dbCourses.update(announcements);
                            final DocumentReference dbAnnouncements = fStore.collection("announcements").document(announcementID);
                            dbAnnouncements.set(announcement).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AnnouncementsInfo.this, "Announcement Added", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AnnouncementsInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }

                }
            }
        });

        final Intent intent2 = new Intent(AnnouncementsInfo.this, AnnouncementsTeacher.class);
        finish();
        startActivity(intent2);


    }
    public void editAnnouncement(){

        final String announcementSubject = announcementSubjectText.getText().toString().trim();
        final String announcementDescription = announcementDescriptionBox.getText().toString();
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        final String date = df.format(currentTime);

        final DocumentReference docRef = fStore.collection("announcements").document(announcementIDBundle);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        docRef.update("subject", announcementSubject);
                        docRef.update("description", announcementDescription);
                        docRef.update("modifiedDate", date).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AnnouncementsInfo.this, "Announcement Edited", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AnnouncementsInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }
        });

        final Intent intent2 = new Intent(AnnouncementsInfo.this, AnnouncementsTeacher.class);
        finish();
        startActivity(intent2);

    }
    public void deleteAnnouncement(){

        final DocumentReference docRef = fStore.collection("announcements").document(announcementIDBundle);
        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AnnouncementsInfo.this, "Announcement Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AnnouncementsInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String courseID = announcementIDBundle.substring(0,7);//CME3030.......
        final DocumentReference assignmentInCourse = fStore.collection("courses").document(courseID);
        assignmentInCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        final ArrayList announcementHolder;
                        announcementHolder = (ArrayList) documentSnapshot.get("announcements");
                        announcementHolder.remove(announcementIDBundle);
                        final Map<String, Object> announcements = new HashMap<>();
                        announcements.put("announcements", announcementHolder);
                        assignmentInCourse.update(announcements);
                    }
                }
            }
        });
        final Intent intent2 = new Intent(AnnouncementsInfo.this, AnnouncementsTeacher.class);
        finish();
        startActivity(intent2);

    }


}
