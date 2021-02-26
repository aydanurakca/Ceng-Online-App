package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadInfo extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView studentNum, studentName, studentWorkBox;
    String stuNumber, stuName, stuWork, assignmentID;
    ArrayList<String> keys= new ArrayList<>();
    ArrayList<String> values= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_info);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        studentName = findViewById(R.id.stu_name_fetched);
        studentNum = findViewById(R.id.stu_number_fetched);
        studentWorkBox = findViewById(R.id.stu_work_box);
        studentWorkBox.setFocusable(false);
        studentWorkBox.setFocusableInTouchMode(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            stuNumber = bundle.getString("studentNumber");
            assignmentID = bundle.getString("assignmentID");
            keys = bundle.getStringArrayList("keys");
            values = bundle.getStringArrayList("values");
        }
        Intent intent = getIntent();

        CollectionReference questionRef = fStore.collection("users");
        questionRef.whereEqualTo("student_number", stuNumber).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot qSnap = task.getResult();
                    if (!qSnap.isEmpty()) {
                        studentName.setText("Student Number: " + qSnap.getDocuments().get(0).get("name").toString());
                        studentNum.setText("Student Name: " + qSnap.getDocuments().get(0).get("student_number").toString());
                        Log.d("TAG", "onComplete: " + keys.get(0));
                        studentWorkBox.setText(values.get(keys.indexOf(qSnap.getDocuments().get(0).getId())));
                    }
                }
            }
            });


        getSupportActionBar().setTitle("Student's Work");
    }
    public boolean onSupportNavigateUp(){
        final Intent intent2 = new Intent(UploadInfo.this,ShowUploadsTeacher.class);
        finish();
        intent2.putExtra("assignmentID", assignmentID);
        startActivity(intent2);
        return true;
    }
}
