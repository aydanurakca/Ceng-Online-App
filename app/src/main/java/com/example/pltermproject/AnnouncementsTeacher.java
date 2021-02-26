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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnnouncementsTeacher extends AppCompatActivity {

    ListView listView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ArrayAdapter arrayAdapter;
    Button addButton;
    String userId, userType, courseName, courseID;
    ArrayList<String> lectures = new ArrayList<>();
    ArrayList<String> announcements = new ArrayList<>();
    ArrayList arrayList = new ArrayList();
    HashMap<String, String> keyMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements_teacher);
        getSupportActionBar().setTitle("Announcements");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        addButton = findViewById(R.id.add_announcement_button);
        listView = findViewById(R.id.announcements_list);
        userId = fAuth.getCurrentUser().getUid();
        final Intent intent = new Intent(AnnouncementsTeacher.this, AnnouncementsInfo.class);
        DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    userType = document.get("UserType").toString();
                    intent.putExtra("UserType",userType);
                    ArrayList lectures = (ArrayList) document.get("lectures");
                    if(document.exists()) {
                        for (int i = 0; i < lectures.size(); i++){
                            CollectionReference questionRef = fStore.collection("announcements");
                            Query query = questionRef.whereEqualTo("course", lectures.get(i));
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot qSnap = task.getResult();
                                        if(!qSnap.isEmpty()){
                                            for (int j = 0; j < task.getResult().getDocuments().size(); j++){
                                                String listString = task.getResult().getDocuments().get(j).get("course").toString()+ " - " + task.getResult().getDocuments().get(j).get("subject").toString();
                                                arrayList.add(listString);
                                                arrayAdapter = new ArrayAdapter(AnnouncementsTeacher.this, android.R.layout.simple_list_item_1, arrayList);
                                                listView.setAdapter(arrayAdapter);
                                                keyMap.put(listString, task.getResult().getDocuments().get(j).getId());
                                            }
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
                intent.putExtra("announcementID",keyMap.get((listView.getItemAtPosition(position).toString())));
                Log.d("TAG", "onItemClick:" + keyMap.get((listView.getItemAtPosition(position).toString())));
                finish();
                startActivity(intent);

            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("announcementID","0000");
                finish();
                startActivity(intent);

            }
        });
    }
    public boolean onSupportNavigateUp(){
        final Intent intent2 = new Intent(AnnouncementsTeacher.this,Teacher.class);
        finish();
        startActivity(intent2);
        return true;
    }
}
