package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.text.BoringLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class AssignmentsInfo extends AppCompatActivity {

    String userId , selectedItem, assignmentID, assignmentIDBundle, courseName, userType;
    TextView dueDateEditBox, descriptionEditBox, assignmentName, assignmentDueDate, assignmentDescription, assignmentNameEditText,assignmentDescriptionText, assignmentDeadlineText, assignmentDeadlineBox, assignmentNameText;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button editButton, deleteButton, uploadButton, showUploadsButton;
    ArrayList<String> lectures = new ArrayList<>();
    ArrayList<String> assignmentsArrayList = new ArrayList<>();
    ArrayList<String> temp = new ArrayList<>();
    ArrayList<String> courses = new ArrayList<>();
    ArrayList<String> students = new ArrayList<>();
    ArrayList<String> students2 = new ArrayList<>();
    ArrayList<String> assignmentNames = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    String [] tokens, tokens2;
    Spinner spinner;
    ImageButton addAssignmentButton, saveEditButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_info);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        addAssignmentButton = findViewById(R.id.add_assignment_button);
        dueDateEditBox = findViewById(R.id.assignment_deadline_box);
        descriptionEditBox = findViewById(R.id.assignment_description_box);
        spinner = findViewById(R.id.courses_spinner);
        assignmentName = findViewById(R.id.assignment_title_text);
        assignmentDueDate = findViewById(R.id.assignment_duedate_text_placeholder);
        assignmentDescription = findViewById(R.id.assignment_description_placeholder);
        assignmentDescriptionText = findViewById(R.id.assignment_description_text);
        assignmentDeadlineText = findViewById(R.id.assignment_deadline_text);
        editButton = findViewById(R.id.assignment_edit_button);
        deleteButton = findViewById(R.id.delete_assignment_button);
        assignmentNameText = findViewById(R.id.assignment_name_text);
        assignmentNameEditText = findViewById(R.id.assignment_name_edit_text);
        saveEditButton = findViewById(R.id.save_edit_button);
        uploadButton = findViewById(R.id.upload_button);
        showUploadsButton = findViewById(R.id.show_uploads_button);

        if(fAuth.getCurrentUser()!= null){
            userId = fAuth.getCurrentUser().getUid();
        }
        getSupportActionBar().setTitle("Assignment Info");

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userType = bundle.getString("UserType");
            if(!(bundle.getString("assignmentID").equals("0000"))){
                assignmentIDBundle = bundle.getString("assignmentID");
                tokens2 = assignmentIDBundle.split("-");
                assignmentIDBundle = tokens2[0].trim() + tokens2[1].substring(1).trim();
            }
            else{
                assignmentIDBundle = bundle.getString("assignmentID");
            }
        }
        if (assignmentIDBundle.equals("0000")) {
            makeEditVisible();
            DocumentReference docRef = fStore.collection("users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
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
                                                Log.d("TAG", "COURSES: " + courses.toString());
                                                arrayAdapter = new ArrayAdapter(AssignmentsInfo.this, android.R.layout.simple_spinner_item, courses);
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
            DocumentReference documentReference = fStore.collection("assignments").document(assignmentIDBundle);
            documentReference.addSnapshotListener(AssignmentsInfo.this, new EventListener<DocumentSnapshot>() {

                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        assignmentName.setText("Name: " + documentSnapshot.get("name").toString());
                        assignmentDueDate.setText("Due Date: " + documentSnapshot.get("dueDate").toString());
                        assignmentDescription.setText("Description: " + documentSnapshot.get("description"));
                        assignmentNameEditText.setText(documentSnapshot.get("name").toString());
                        dueDateEditBox.setText(documentSnapshot.get("dueDate").toString());
                        descriptionEditBox.setText(documentSnapshot.get("description").toString());

                    }
                }
            });

        }
        Log.d("TAG", "onCreate: "+ userType);
        if(userType.equals("student")){
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            uploadButton.setVisibility(View.VISIBLE);

        }
        else if(userType.equals("teacher") && !(assignmentIDBundle.equals("0000"))){
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.GONE);
            showUploadsButton.setVisibility(View.VISIBLE);

        }
        addAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAssignment();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditVisible();
                spinner.setVisibility(View.GONE);
                showUploadsButton.setVisibility(View.GONE);
                saveEditButton.setVisibility(View.VISIBLE);
            }
        });
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAssignment();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAssignment();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent2 = new Intent(AssignmentsInfo.this, UploadWork.class);
                intent2.putExtra("assignmentName", assignmentName.getText().toString());
                intent2.putExtra("assignmentDueDate", assignmentDueDate.getText().toString());
                String [] temp = assignmentDescription.getText().toString().split(":");
                intent2.putExtra("assignmentDescription", temp[1].substring(1));
                intent2.putExtra("assignmentID",bundle.get("assignmentID").toString());
                descriptionEditBox.setFocusable(false);
                descriptionEditBox.setFocusableInTouchMode(false);
                finish();
                startActivity(intent2);
            }
        });
        showUploadsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent2 = new Intent(AssignmentsInfo.this, ShowUploadsTeacher.class);
                intent2.putExtra("assignmentName", assignmentName.getText().toString());
                intent2.putExtra("assignmentDueDate", assignmentDueDate.getText().toString());
                String [] temp = assignmentDescription.getText().toString().split(":");
                intent2.putExtra("assignmentDescription", temp[1].substring(1));
                intent2.putExtra("assignmentID",bundle.get("assignmentID").toString());
                descriptionEditBox.setFocusable(false);
                descriptionEditBox.setFocusableInTouchMode(false);
                finish();
                startActivity(intent2);
            }
        });

    }

    public boolean onSupportNavigateUp(){
        if (userType.equals("teacher")) {
            final Intent intent2 = new Intent(AssignmentsInfo.this,AssignmentsTeacher.class);
            finish();
            startActivity(intent2);
        }else if (userType.equals("student")) {
            final Intent intent2 = new Intent(AssignmentsInfo.this,AssignmentsStudent.class);
            finish();
            startActivity(intent2);
        }

        return true;
    }

    public void addAssignment(){
        final String name = assignmentNameEditText.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            assignmentNameEditText.setError("Assignment Name is required!");
            return;
        }
        String description = descriptionEditBox.getText().toString().trim();
        String dueDate = dueDateEditBox.getText().toString().trim();
        if(TextUtils.isEmpty(dueDate)){
            dueDateEditBox.setError("Due Date is required!");
            return;
        }
        selectedItem = spinner.getSelectedItem().toString();
        tokens = selectedItem.split("-");
        tokens[0] = tokens[0].trim();
        assignmentID = tokens[0] + name; // CME3206MANSim
        final Map<String, String> filler = new HashMap<>();
        final Assignment assignment = new Assignment(description, dueDate, name, filler, tokens[0]);
        final DocumentReference dbCourses = fStore.collection("courses").document(tokens[0]);
        final DocumentReference dbAssignments = fStore.collection("assignments").document(assignmentID);
        final Map<String, Object> assignments = new HashMap<>();
        dbCourses.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        temp = (ArrayList) document.get("assignments");
                        if(temp != null){
                            assignmentsArrayList.addAll(temp);
                        }
                        if(!(temp.contains(assignmentID))){
                            assignmentsArrayList.add(assignmentID);
                            assignments.put("assignments", assignmentsArrayList);
                            dbCourses.update(assignments);
                            dbAssignments.set(assignment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AssignmentsInfo.this, "Assignment Added", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AssignmentsInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(AssignmentsInfo.this, "Assignment already exists!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                }
            }
        });
        final DocumentReference docRef = fStore.collection("courses").document(tokens[0]);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        temp = (ArrayList) document.get("students");
                        for (int i = 0; i < temp.size(); i++) {
                            final DocumentReference courseInStudent = fStore.collection("users").document(temp.get(i));
                            courseInStudent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()) {
                                            ArrayList assignmentHolder;
                                            assignmentHolder = (ArrayList) documentSnapshot.get("assignments");
                                            assignmentHolder.add(assignmentID);
                                            final Map<String, Object> assignments = new HashMap<>();
                                            assignments.put("assignments", assignmentHolder);
                                            courseInStudent.update(assignments);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        final Intent intent2 = new Intent(AssignmentsInfo.this, AssignmentsTeacher.class);
        finish();
        startActivity(intent2);
    }

    public void makeEditVisible() {
        assignmentNameEditText.setVisibility(View.VISIBLE);
        assignmentNameText.setVisibility(View.VISIBLE);
        descriptionEditBox.setVisibility(View.VISIBLE);
        assignmentDescriptionText.setVisibility(View.VISIBLE);
        assignmentDeadlineText.setVisibility(View.VISIBLE);
        dueDateEditBox.setVisibility(View.VISIBLE);
        addAssignmentButton.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);

        assignmentName.setVisibility(View.GONE);
        assignmentDueDate.setVisibility(View.GONE);
        assignmentDescription.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
    }

    public void editAssignment(){

        final String assgnName = assignmentNameEditText.getText().toString().trim();
        final String courseName = assignmentIDBundle.substring(0,7);
        final String dueDateText = dueDateEditBox.getText().toString().trim();
        final String descriptionText = descriptionEditBox.getText().toString().trim();

        final DocumentReference docRef = fStore.collection("courses").document(courseName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        assignmentsArrayList = (ArrayList) document.get("assignments");
                        students = (ArrayList) document.get("students");
                        for (int i = 0; i < students.size(); i++){
                            final DocumentReference docRef = fStore.collection("users").document(students.get(i));
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            ArrayList<String> assignments = assignmentsArrayList = (ArrayList) document.get("assignments");
                                            int index = assignmentsArrayList.indexOf(assignmentIDBundle);
                                            assignments.set(index, courseName + assgnName);
                                            docRef.update("assignments", assignments);
                                        }
                                    }
                                }
                            });
                        }
                        int index = assignmentsArrayList.indexOf(assignmentIDBundle);
                        assignmentsArrayList.set(index, courseName + assgnName);
                        docRef.update("assignments", assignmentsArrayList);
                    }
                }
            }
        });
        final DocumentReference documentReference = fStore.collection("assignments").document(assignmentIDBundle);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Map<String, String> uploads = new HashMap<>();
                        uploads = (Map<String, String>) documentSnapshot.get("uploads");
                        String path = courseName + assgnName;
                        Log.d("TAG", "path: " + path);
                        final DocumentReference documentRef = fStore.collection("assignments").document(path);
                        final Map<String, String> finalUploads = uploads;
                        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    Assignment assignment = new Assignment(descriptionText, dueDateText, assgnName, finalUploads, courseName);
                                    documentRef.set(assignment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AssignmentsInfo.this, "Assignment Edited", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(AssignmentsInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        documentReference.delete();
                    }
                }
            }
        });



        final Intent intent2 = new Intent(AssignmentsInfo.this, AssignmentsTeacher.class);
        finish();
        startActivity(intent2);

    }

    public void deleteAssignment(){

        final DocumentReference docRef = fStore.collection("assignments").document(assignmentIDBundle);
        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AssignmentsInfo.this, "Assignment Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AssignmentsInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String courseID = assignmentIDBundle.substring(0,7);//CME3030.......
        final DocumentReference assignmentInCourse = fStore.collection("courses").document(courseID);
        assignmentInCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        final ArrayList assignmentHolder;
                        assignmentHolder = (ArrayList) documentSnapshot.get("assignments");
                        assignmentHolder.remove(assignmentIDBundle);
                        final Map<String, Object> assignments = new HashMap<>();
                        assignments.put("assignments", assignmentHolder);
                        assignmentInCourse.update(assignments);
                        assignmentInCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        students2 = (ArrayList) document.get("students");
                                        for (int i = 0; i < students2.size(); i++) {
                                            final DocumentReference docRef = fStore.collection("users").document(students2.get(i));
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        DocumentSnapshot document = task.getResult();
                                                        if(document.exists()){
                                                            assignmentNames = (ArrayList) document.get("assignments");
                                                            assignmentNames.remove(assignmentIDBundle);
                                                            final Map<String, Object> assignments = new HashMap<>();
                                                            assignments.put("assignments", assignmentNames);
                                                            docRef.update(assignments);
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
                }
            }
        });

        final Intent intent2 = new Intent(AssignmentsInfo.this, AssignmentsTeacher.class);
        finish();
        startActivity(intent2);

    }


}

