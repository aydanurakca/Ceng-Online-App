package com.example.pltermproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class SentMessageInfo extends AppCompatActivity {

    TextView messageText, subjectText, dateText, receiverText;
    String [] tokens;
    String selectedItem, messageID, receiverName, userType;
    ArrayList<String> keys= new ArrayList<>();
    ArrayList<String> values= new ArrayList<>();
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Intent intent2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_message_info);
        messageText = findViewById(R.id.sent_message_text);
        subjectText = findViewById(R.id.subject_text_sent);
        dateText = findViewById(R.id.date_text_sent);
        receiverText = findViewById(R.id.receiver_text_sent);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedItem = bundle.getString("selectedItem");
            tokens = selectedItem.split("First Line=");
            selectedItem=tokens[1].substring(0,tokens[1].length()-1);
            Log.d("selected", selectedItem);
            keys = bundle.getStringArrayList("keys");
            values = bundle.getStringArrayList("values");
            messageID = values.get(keys.indexOf(selectedItem));
            tokens = selectedItem.split("-");
            receiverName = tokens[0].trim();
            userType = bundle.getString("UserType");
        }
        DocumentReference documentReference = fStore.collection("messages").document(messageID);
        documentReference.addSnapshotListener(SentMessageInfo.this, new EventListener<DocumentSnapshot>() {

            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    getSupportActionBar().setTitle(documentSnapshot.get("subject").toString());
                    receiverText.setText("Receiver: " + receiverName);
                    dateText.setText("Date: " + documentSnapshot.get("date").toString());
                    subjectText.setText("Subject: " + documentSnapshot.get("subject"));
                    messageText.setText(documentSnapshot.get("message").toString());
                }
            }
        });

        intent2 = new Intent(SentMessageInfo.this,SentMessages.class);
        intent2.putExtra("UserType", userType);

    }
    public boolean onSupportNavigateUp(){

        finish();
        startActivity(intent2);
        return true;
    }
}
