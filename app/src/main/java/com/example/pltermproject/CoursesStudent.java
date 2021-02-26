package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CoursesStudent extends AppCompatActivity {


    ListView listView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    String userType;
    ArrayAdapter arrayAdapter;
    ArrayList<String> lectures = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_student);

        getSupportActionBar().setTitle("Your Courses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.courses_list2);
        if(fAuth.getCurrentUser()!= null){
            userId = fAuth.getCurrentUser().getUid();
        }
        final Intent intent = new Intent(CoursesStudent.this, Stream.class);
        DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        lectures = (ArrayList) document.get("lectures");
                        arrayAdapter = new ArrayAdapter(CoursesStudent.this, android.R.layout.simple_list_item_1,lectures);
                        listView.setAdapter(arrayAdapter);
                        userType = document.get("UserType").toString();
                        intent.putExtra("UserType",userType);
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("CourseId",listView.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });
    }
    public boolean onSupportNavigateUp(){
        final Intent intent2 = new Intent(CoursesStudent.this,Student.class);
        startActivity(intent2);
        finish();
        return true;
    }
}
