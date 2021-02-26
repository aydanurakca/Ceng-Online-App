package com.example.pltermproject;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CourseInfo extends AppCompatActivity {

    TextView courseName, courseGrade, courseSection, courseLabteacher, courseTerm, courseCode, courseTitle, titleText, courseidText, coursenameText, gradeText, termText, labteacherText, sectionsText;
    EditText courseNameEditText, courseGradeEditText, courseSectionEditText, courseLabteacherEditText, courseTermEditText, courseCodeEditText, courseTitleEditText;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    public static Boolean isExisting = false;
    String courseId, teacher, code;
    protected String userType;
    Button editButton, deleteButton, addButton;
    ImageButton saveButton, saveEditButton;
    ArrayList<String> lecturesArrayList = new ArrayList<>();
    ArrayList<String> temp = new ArrayList<>();
    ArrayList<String> assignmentsArrayList = new ArrayList<>();
    ArrayList<String> announcementsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        courseName = findViewById(R.id.course_name_text);
        courseGrade = findViewById(R.id.course_grade_text);
        courseSection = findViewById(R.id.course_sections_text);
        courseLabteacher = findViewById(R.id.course_labteacher_text);
        courseTerm = findViewById(R.id.course_term_text);
        courseCode = findViewById(R.id.course_id_text);
        courseTitle = findViewById(R.id.course_title_text);
        courseNameEditText = findViewById(R.id.course_name_edit_text);
        courseGradeEditText = findViewById(R.id.course_grade_edit_text);
        courseSectionEditText = findViewById(R.id.course_sections_edit_text);
        courseLabteacherEditText = findViewById(R.id.course_labteacher_edit_text);
        courseTermEditText = findViewById(R.id.course_term_edit_text);
        courseCodeEditText = findViewById(R.id.course_id_edit_text);
        courseTitleEditText = findViewById(R.id.course_title_edit_text);
        titleText = findViewById(R.id.title);
        courseidText = findViewById(R.id.courseid);
        coursenameText = findViewById(R.id.coursename);
        gradeText = findViewById(R.id.grade);
        termText = findViewById(R.id.term);
        labteacherText = findViewById(R.id.labteacher);
        sectionsText = findViewById(R.id.sections);
        saveButton = findViewById(R.id.save_button);
        editButton = findViewById(R.id.course_edit_button);
        deleteButton = findViewById(R.id.delete_course_button);
        saveEditButton = findViewById(R.id.save_edit_button);


        getSupportActionBar().setTitle("CourseInfo");
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            courseId = bundle.getString("CourseId");
            userType = bundle.getString("UserType");
        }
        if (courseId.equals("0000")) {
            makeEditVisible();
        } else {
            DocumentReference documentReference = fStore.collection("courses").document(courseId);
            documentReference.addSnapshotListener(CourseInfo.this, new EventListener<DocumentSnapshot>() {

                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        courseName.setText("Name: " + documentSnapshot.get("name").toString());
                        courseNameEditText.setText(documentSnapshot.get("name").toString());
                        courseTitle.setText(documentSnapshot.get("code").toString());
                        courseTitleEditText.setText(documentSnapshot.get("code").toString());
                        courseCode.setText("Code: " + documentSnapshot.get("code").toString());
                        courseCodeEditText.setText(documentSnapshot.get("code").toString());
                        courseGrade.setText("Grade: " + documentSnapshot.get("grade").toString());
                        courseGradeEditText.setText(documentSnapshot.get("grade").toString());
                        courseLabteacher.setText("Lab Teacher: " + documentSnapshot.get("labteacher").toString());
                        courseLabteacherEditText.setText(documentSnapshot.get("labteacher").toString());
                        courseSection.setText("Sections: " + documentSnapshot.get("sections").toString());
                        courseSectionEditText.setText(documentSnapshot.get("sections").toString());
                        courseTerm.setText("Term: " + documentSnapshot.get("term").toString());
                        courseTermEditText.setText(documentSnapshot.get("term").toString());
                    }
                }
            });
        }

        final DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                teacher = documentSnapshot.get("name").toString();
            }
        });
        if (userType.equals("student")) {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);

        } else if (userType.equals("teacher") && !(courseId.equals("0000"))) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);

        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCourse();

            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseTitleEditText.setFocusable(false);
                courseCodeEditText.setFocusable(false);
                courseCodeEditText.setFocusableInTouchMode(false);
                courseTitleEditText.setFocusableInTouchMode(false);
                saveButton.setVisibility(View.GONE);
                saveEditButton.setVisibility(View.VISIBLE);
                makeEditVisible();
            }
        });
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditButton();
                final Intent intent2 = new Intent(CourseInfo.this, Stream.class);
                intent2.putExtra("UserType",userType);
                intent2.putExtra("CourseId",courseId);
                finish();
                startActivity(intent2);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCurrentCourse();
                final Intent intent2 = new Intent(CourseInfo.this, CoursesTeacher.class);
                finish();
                startActivity(intent2);
            }
        });

    }

    public boolean onSupportNavigateUp() {
        if (userType.equals("teacher") && !courseId.equals("0000")) {
            final Intent intent = new Intent(CourseInfo.this, Stream.class);
            intent.putExtra("UserType",userType);
            intent.putExtra("CourseId",courseId);
            finish();
            startActivity(intent);


        }else  if (userType.equals("teacher") && courseId.equals("0000")) {

            final Intent intent = new Intent(CourseInfo.this, CoursesTeacher.class);
            intent.putExtra("UserType",userType);
            intent.putExtra("CourseId",courseId);
            finish();
            startActivity(intent);


        } else if (userType.equals("student")) {
            final Intent intent = new Intent(CourseInfo.this, Stream.class);
            intent.putExtra("UserType",userType);
            intent.putExtra("CourseId",courseId);
            finish();
            startActivity(intent);

        }

        return true;
    }

    public void makeEditVisible() {
        courseNameEditText.setVisibility(View.VISIBLE);
        courseCodeEditText.setVisibility(View.VISIBLE);
        courseTitleEditText.setVisibility(View.VISIBLE);
        courseTermEditText.setVisibility(View.VISIBLE);
        courseGradeEditText.setVisibility(View.VISIBLE);
        courseLabteacherEditText.setVisibility(View.VISIBLE);
        courseSectionEditText.setVisibility(View.VISIBLE);
        titleText.setVisibility(View.VISIBLE);
        courseidText.setVisibility(View.VISIBLE);
        coursenameText.setVisibility(View.VISIBLE);
        gradeText.setVisibility(View.VISIBLE);
        termText.setVisibility(View.VISIBLE);
        labteacherText.setVisibility(View.VISIBLE);
        sectionsText.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);


        courseName.setVisibility(View.GONE);
        courseGrade.setVisibility(View.GONE);
        courseTitle.setVisibility(View.GONE);
        courseCode.setVisibility(View.GONE);
        courseLabteacher.setVisibility(View.GONE);
        courseTerm.setVisibility(View.GONE);
        courseSection.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
    }

    public void saveCourse() {
        code = courseTitleEditText.getText().toString().trim();
        if(TextUtils.isEmpty(code)){
            courseTitleEditText.setError("Course Code is required.");
            return;
        }
        final String grade = courseGradeEditText.getText().toString().trim();
        String labteacher = courseLabteacherEditText.getText().toString().trim();
        String name = courseNameEditText.getText().toString().trim();
        String sections = courseSectionEditText.getText().toString().trim();
        String term = courseTermEditText.getText().toString().trim();
        ArrayList<String> students = new ArrayList<>();
        ArrayList<String> assignments = new ArrayList<>();
        ArrayList<String> announcements = new ArrayList<>();
        final Course course = new Course(code, teacher, grade, name, term, labteacher, sections, students, assignments, announcements);
        final DocumentReference dbCourses = fStore.collection("courses").document(code);

        final DocumentReference dbUsers = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        final Map<String, Object> lectures = new HashMap<>();
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        temp = (ArrayList) document.get("lectures");
                        lecturesArrayList.addAll(temp);
                        CollectionReference coursesRef = fStore.collection("courses");
                        coursesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Course course1 = document.toObject(Course.class);
                                        if(course1.getCode().equals(code)){
                                            isExisting = true;
                                            break;
                                        }
                                    }
                                    if(!(lecturesArrayList.contains(code)) && !(isExisting)){
                                        lecturesArrayList.add(code);
                                        lectures.put("lectures", lecturesArrayList);
                                        dbUsers.update(lectures);
                                        dbCourses.set(course).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CourseInfo.this, "Course Added", Toast.LENGTH_SHORT).show();
                                                    final Intent intent2 = new Intent(CourseInfo.this, CoursesTeacher.class);
                                                    finish();
                                                    startActivity(intent2);
                                                    return;
                                                } else {
                                                    Toast.makeText(CourseInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(CourseInfo.this, "Course already exists!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                        });

                    }

                }
            }
        });


    }

    public void saveEditButton() {

        code = courseTitleEditText.getText().toString().trim();
        final String grade = courseGradeEditText.getText().toString().trim();
        final String labteacher = courseLabteacherEditText.getText().toString().trim();
        final String name = courseNameEditText.getText().toString().trim();
        final String sections = courseSectionEditText.getText().toString().trim();
        final String term = courseTermEditText.getText().toString().trim();
        DocumentReference docRef = fStore.collection("courses").document(code);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        temp = (ArrayList) document.get("students");
                        assignmentsArrayList = (ArrayList) document.get("assignments");
                        announcementsArrayList = (ArrayList) document.get("announcements");
                        Course course = new Course(code, teacher, grade, name, term, labteacher, sections, temp, assignmentsArrayList, announcementsArrayList);
                        DocumentReference dbCourses = fStore.collection("courses").document(code);
                        dbCourses.set(course).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CourseInfo.this, "Course Edited", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CourseInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            }
        });



    }

    public void deleteCurrentCourse() {
        final DocumentReference docRef = fStore.collection("courses").document(courseId);
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
                                            ArrayList courseHolder;
                                            courseHolder = (ArrayList) documentSnapshot.get("lectures");
                                            courseHolder.remove(courseId);
                                            final Map<String, Object> lectures = new HashMap<>();
                                            lectures.put("lectures", courseHolder);
                                            courseInStudent.update(lectures);

                                        }
                                    }
                                }

                            });
                            courseInStudent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot.exists()){
                                            ArrayList assignmentHolder;
                                            assignmentHolder = (ArrayList) documentSnapshot.get("assignments");
                                            for (Object assignment: assignmentHolder) {
                                                String temp = assignment.toString().substring(0,7);

                                                if(temp.equals(courseId)){
                                                    assignmentHolder.remove(assignment);
                                                    final Map<String, Object> assignments = new HashMap<>();
                                                    assignments.put("assignments", assignmentHolder);
                                                    courseInStudent.update(assignments);
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        final DocumentReference courseInTeacher = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                        courseInTeacher.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        ArrayList courseHolder;
                                        courseHolder = (ArrayList) documentSnapshot.get("lectures");
                                        courseHolder.remove(courseId);
                                        final Map<String, Object> lectures = new HashMap<>();
                                        lectures.put("lectures", courseHolder);
                                        courseInTeacher.update(lectures);
                                    }
                                }
                            }
                        });
                        fStore.collection("assignments").whereEqualTo("course", courseId).get().addOnCompleteListener(CourseInfo.this, new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                                    {
                                        fStore.collection("assignments").document(documentSnapshot.getId()).delete();
                                    }
                                }
                            }
                        });
                        fStore.collection("announcements").whereEqualTo("course", courseId).get().addOnCompleteListener(CourseInfo.this, new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                                    {
                                        fStore.collection("announcements").document(documentSnapshot.getId()).delete();
                                    }
                                }
                            }
                        });
                        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CourseInfo.this, "Course Deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CourseInfo.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }


            }
        });

    }

}