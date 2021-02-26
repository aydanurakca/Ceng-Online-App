package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersList extends AppCompatActivity {

    ListView listView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ArrayAdapter arrayAdapter;
    ArrayList<String> users = new ArrayList<>();
    HashMap<String, String> keyMap = new HashMap<>();
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        listView = findViewById(R.id.users_list);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        getSupportActionBar().setTitle("Users");

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            userType = bundle.getString("UserType");
        }

        final Intent intent = new Intent(UsersList.this, MessageReply.class);

        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(!document.getId().equals(fAuth.getCurrentUser().getUid())){
                            list.add(document.get("name").toString());
                            keyMap.put(document.get("name").toString(), document.getId());
                        }
                    }

                    arrayAdapter = new ArrayAdapter(UsersList.this, android.R.layout.simple_list_item_1,list);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("receiverId",keyMap.get(listView.getItemAtPosition(position).toString()));
                intent.putExtra("fromId","0000");
                intent.putExtra("UserType", userType);
                finish();
                startActivity(intent);
            }
        });
    }
    public boolean onSupportNavigateUp(){
        final Intent intent2 = new Intent(UsersList.this,InboxList.class);
        intent2.putExtra("UserType", userType);
        startActivity(intent2);
        finish();
        return true;
    }

}
