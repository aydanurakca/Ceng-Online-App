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
import android.widget.SimpleAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SentMessages extends AppCompatActivity {

    ListView listView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ArrayList<HashMap<String,String>> messages = new ArrayList<HashMap<String,String>>();
    ArrayAdapter arrayAdapter;
    String userType, temp, receiverSubject;
    String [] datesArray = new String[10];
    final List<String[]> messageList = new LinkedList<String[]>();
    HashMap<String, String> keyMap = new HashMap<>();
    List<HashMap<String,String>> realListItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_messages);
        listView = findViewById(R.id.sent_messages_list);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        getSupportActionBar().setTitle("Sent Messages");


        final Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            userType = bundle.getString("UserType");
        }

        final Intent intent = new Intent(SentMessages.this, SentMessageInfo.class);

        final List<HashMap<String,String>> listItems = new ArrayList<>();
        final SimpleAdapter adapter = new SimpleAdapter(SentMessages.this, realListItems, android.R.layout.simple_list_item_2, new String[]{"First Line", "Second Line"}, new int []{android.R.id.text1, android.R.id.text2});

        CollectionReference questionRef = fStore.collection("messages");
        questionRef.whereEqualTo("sender", fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot qSnap = task.getResult();
                    final Stack[] stack = {new Stack(task.getResult().getDocuments().size())};
                    final HashMap<String,String> datesMap = new HashMap<>();
                    if(!qSnap.isEmpty()){
                        for (DocumentSnapshot doc: task.getResult().getDocuments() ) {
                            stack[0].push(doc.get("date"));
                            receiverSubject = doc.get("receiverName") + " - " + doc.get("subject");
                            datesMap.put(doc.get("date").toString(), receiverSubject);
                        }
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++){
                            DocumentReference docRef = fStore.collection("users").document(task.getResult().getDocuments().get(i).get("receiver").toString());
                            final int finalI = i;
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                    if(task2.isSuccessful()){
                                        DocumentSnapshot document = task2.getResult();
                                        temp = document.get("name").toString() + " - " + task.getResult().getDocuments().get(finalI).get("subject").toString();
                                        intent.putExtra("receiver",document.get("name").toString());
                                        keyMap.put(temp,task.getResult().getDocuments().get(finalI).getId());
                                        Log.d("key:", keyMap.get(temp));
                                        ArrayList<String> keys= new ArrayList<>();
                                        ArrayList<String> values= new ArrayList<>();
                                        for (String key : keyMap.keySet()) {
                                            keys.add(key);
                                            values.add(keyMap.get(key));
                                        }
                                        intent.putExtra("keys", keys);
                                        intent.putExtra("values", values);
                                    }

                                }
                            });
                        }
                    }
                    try {
                        stack[0] = stack[0].sortstack(stack[0]);
                        while (!stack[0].isEmpty())
                        {
                            HashMap<String,String> newMap = new HashMap<>();
                            newMap.put("First Line", datesMap.get(stack[0].peek().toString()));
                            newMap.put("Second Line", stack[0].peek().toString());
                            realListItems.add(newMap);
                            stack[0].pop();
                        }
                        listView.setAdapter(adapter);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("selectedItem", listView.getItemAtPosition(position).toString());
                intent.putExtra("UserType", userType);
                finish();
                startActivity(intent);
            }
        });
    }
    public boolean onSupportNavigateUp(){
        final Intent intent2 = new Intent(SentMessages.this,InboxList.class);
        intent2.putExtra("UserType", userType);
        finish();
        startActivity(intent2);
        return true;
    }
}
