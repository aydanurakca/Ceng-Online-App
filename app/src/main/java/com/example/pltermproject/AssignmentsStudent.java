package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AssignmentsStudent extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button addButton;
    ListView listView;
    String userId;
    ArrayAdapter arrayAdapter;
    ArrayList<String> lectures = new ArrayList<>();
    ArrayList<String> assignments = new ArrayList<>();
    ArrayList<String> assignmentNames = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_student);
        listView = findViewById(R.id.assignments_list_student);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        getSupportActionBar().setTitle("Your Assignments");
        if(fAuth.getCurrentUser()!= null){
            userId = fAuth.getCurrentUser().getUid();
        }
        final Intent intent = new Intent(AssignmentsStudent.this, AssignmentsInfo.class);
        final DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            assignmentNames = (ArrayList) document.get("assignments");
                                            if(assignmentNames != null){
                                                for (int j = 0; j < assignmentNames.size(); j++){
                                                    String temp = assignmentNames.get(j).substring(0,7);
                                                    assignmentNames.set(j, temp + " - " + assignmentNames.get(j).substring(7));
                                                    assignments.add(assignmentNames.get(j));
                                                }
                                                arrayAdapter = new ArrayAdapter(AssignmentsStudent.this, android.R.layout.simple_list_item_1, assignments);
                                                listView.setAdapter(arrayAdapter);
                                            }

                                        }
                                    }
                                }
                            });
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("assignmentID",listView.getItemAtPosition(position).toString());
                final DocumentReference docRef = fStore.collection("users").document(userId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                intent.putExtra("UserType", document.get("UserType").toString());
                                startActivity(intent);
                            }
                        }
                    }
                });

            }
        });

    }
    public boolean onSupportNavigateUp(){
        final Intent intent2 = new Intent(AssignmentsStudent.this,Student.class);
        finish();
        startActivity(intent2);
        return true;
    }
}
