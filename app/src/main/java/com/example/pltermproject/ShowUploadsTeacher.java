package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowUploadsTeacher extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String assignmentID, assignmentIDfixed, stuName, stuWork;
    String [] tokens2;
    ArrayList<String> students = new ArrayList<>();
    ArrayList<String> keys= new ArrayList<>();
    ArrayList<String> values= new ArrayList<>();
    Map<String, String> uploads = new HashMap<>();
    ArrayAdapter arrayAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_uploads_teacher);

        listView = findViewById(R.id.uploads_list);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            assignmentID = bundle.getString("assignmentID");
        }
        assignmentIDfixed = assignmentID;
        tokens2 = assignmentIDfixed.split("-");
        assignmentIDfixed = tokens2[0].trim() + tokens2[1].substring(1).trim();
        final Intent intent2 = new Intent(ShowUploadsTeacher.this,UploadInfo.class);
        DocumentReference docRef = fStore.collection("assignments").document(assignmentIDfixed);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                            uploads = (HashMap<String, String>) document.get("uploads");
                            for (String key : uploads.keySet()) {
                                keys.add(key);
                                values.add(uploads.get(key));
                            }
                            intent2.putExtra("keys", keys);
                            intent2.putExtra("values", values);
                            for(final HashMap.Entry<String, String> entry : uploads.entrySet()){
                                DocumentReference docRef = fStore.collection("users").document(entry.getKey());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String stuNumber = document.get("student_number").toString();
                                                stuName = document.get("name").toString();
                                                stuWork = entry.getValue();
                                                students.add(stuNumber);
                                                arrayAdapter = new ArrayAdapter(ShowUploadsTeacher.this, android.R.layout.simple_list_item_1, students);
                                                listView.setAdapter(arrayAdapter);
                                            }
                                        }
                                    }
                                });
                            }


                        }
                    }
                }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                intent2.putExtra("studentNumber",listView.getItemAtPosition(position).toString());
                intent2.putExtra("studentName", stuName);
                intent2.putExtra("studentWork", stuWork);
                intent2.putExtra("assignmentID", assignmentID);
                finish();
                startActivity(intent2);
            }
        });
        getSupportActionBar().setTitle("Student Uploads");
    }
    public boolean onSupportNavigateUp(){
        final Intent intent2 = new Intent(ShowUploadsTeacher.this,AssignmentsInfo.class);
        finish();
        intent2.putExtra("assignmentID", assignmentID);
        intent2.putExtra("UserType", "teacher");
        startActivity(intent2);
        return true;
    }

}
