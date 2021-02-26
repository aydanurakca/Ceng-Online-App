package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class MessageReply extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ImageButton sendButton;
    TextView senderText, dateText, subjectText, messageText, replyText, subjectPlaceholder, yourMessageBox, subjectEditText, yourMessagePlaceholder, yourReplyPlaceholder;
    String selectedItem, senderName, messageID, userType;
    ArrayList<String> keys= new ArrayList<>();
    ArrayList<String> values= new ArrayList<>();
    String[] tokens;
    String fromId, receiverId;
    Intent intent2, intent3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_reply);
        sendButton = findViewById(R.id.send_button);
        senderText= findViewById(R.id.sender_text);
        dateText = findViewById(R.id.date_text);
        subjectText = findViewById(R.id.subject_text);
        messageText = findViewById(R.id.message_box);
        replyText = findViewById(R.id.reply_box);
        yourReplyPlaceholder = findViewById(R.id.title);
        subjectPlaceholder = findViewById(R.id.subject_placeholder);
        yourMessageBox = findViewById(R.id.your_message_box);
        subjectEditText = findViewById(R.id.subject_edit_text);
        yourMessagePlaceholder = findViewById(R.id.your_message_text);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        intent2 = new Intent(MessageReply.this,UsersList.class);
        intent3 = new Intent(MessageReply.this,InboxList.class);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fromId = bundle.getString("fromId");
            if(!fromId.equals("0000")){
                selectedItem = bundle.getString("selectedItem");
                tokens = selectedItem.split("First Line=");
                selectedItem=tokens[1].substring(0,tokens[1].length()-1);
                Log.d("selected", selectedItem);
                keys = bundle.getStringArrayList("keys");
                values = bundle.getStringArrayList("values");
                messageID = values.get(keys.indexOf(selectedItem));
                tokens = selectedItem.split("-");
                senderName = tokens[0].trim();

            }
            userType = bundle.getString("UserType");
            intent2.putExtra("UserType", userType);
            intent3.putExtra("UserType", userType);
        }
        if(fromId.equals("0000")){
            getSupportActionBar().setTitle("Send Message");
            receiverId = bundle.getString("receiverId");
            newMessageMode();
        }
        if(fromId.equals("1111")){
            getSupportActionBar().setTitle("Reply Message");
            replyMessageMode();
        }
        if(messageID!=null){
            DocumentReference documentReference = fStore.collection("messages").document(messageID);
            documentReference.addSnapshotListener(MessageReply.this, new EventListener<DocumentSnapshot>() {

                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        senderText.setText("Sender: " + senderName);
                        dateText.setText("Date: " + documentSnapshot.get("date").toString());
                        subjectText.setText("Subject: " + documentSnapshot.get("subject"));
                        messageText.setText(documentSnapshot.get("message").toString());
                    }
                }
            });
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromId.equals("0000")){
                    addMessage();
                }
                else if(fromId.equals("1111")){
                    String subject = subjectText.getText().toString().substring(9);
                    addMessage(subject);
                }

            }
        });

    }
    public boolean onSupportNavigateUp(){
        if(fromId.equals("0000")){
            finish();
            startActivity(intent2);
        }
        else if(fromId.equals("1111")){
            finish();
            startActivity(intent3);
        }
        return true;
    }

    public void newMessageMode(){
        senderText.setVisibility(View.GONE);
        dateText.setVisibility(View.GONE);
        subjectText.setVisibility(View.GONE);
        messageText.setVisibility(View.GONE);
        replyText.setVisibility(View.GONE);
        yourReplyPlaceholder.setVisibility(View.GONE);
        subjectPlaceholder.setVisibility(View.VISIBLE);
        yourMessageBox.setVisibility(View.VISIBLE);
        subjectEditText.setVisibility(View.VISIBLE);
        yourMessagePlaceholder.setVisibility(View.VISIBLE);
    }
    public void replyMessageMode(){
        subjectPlaceholder.setVisibility(View.GONE);
        subjectEditText.setVisibility(View.GONE);
    }
    public void addMessage(final String subject){
        final DocumentReference documentReference = fStore.collection("messages").document(messageID);
        documentReference.addSnapshotListener(MessageReply.this, new EventListener<DocumentSnapshot>() {
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    final String receiver = documentSnapshot.get("sender").toString();
                    final String sender = fAuth.getCurrentUser().getUid();
                    final Date date = Calendar.getInstance().getTime();
                    final String messageText = replyText.getText().toString();
                    if(TextUtils.isEmpty(messageText)){
                        replyText.setError("Message is required.");
                        return;
                    }
                    final String replySubject = "RE: " + subject;
                    DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                final DocumentSnapshot document = task.getResult();
                                final String senderName = document.get("name").toString();
                                if (document.exists()) {
                                    DocumentReference docRef = fStore.collection("users").document(receiver);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    String receiverName = document.get("name").toString();
                                                    Message message = new Message(sender, receiver, date, replySubject, messageText, receiverName, senderName );
                                                    final DocumentReference dbMessages = fStore.collection("messages").document();
                                                    dbMessages.set(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(MessageReply.this, "Reply Sent", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(MessageReply.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                                            }
                                                            final Intent intent2 = new Intent(MessageReply.this,InboxList.class);
                                                            intent2.putExtra("UserType", userType);
                                                            finish();
                                                            startActivity(intent2);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    });

                }
            }
        });

    }
    public void addMessage(){
        final String receiver = receiverId;
        final String sender = fAuth.getCurrentUser().getUid();
        final Date date = Calendar.getInstance().getTime();
        final String messageText = yourMessageBox.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            yourMessageBox.setError("Message is required.");
            return;
        }
        final String subject = subjectEditText.getText().toString();
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    final String senderName = document.get("name").toString();
                    if (document.exists()) {
                        DocumentReference docRef = fStore.collection("users").document(receiver);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String receiverName = document.get("name").toString();
                                        Message message = new Message(sender, receiver, date, subject, messageText, receiverName, senderName);
                                        final DocumentReference dbMessages = fStore.collection("messages").document();
                                        dbMessages.set(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MessageReply.this, "Message Sent", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MessageReply.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                                }
                                                final Intent intent2 = new Intent(MessageReply.this,InboxList.class);
                                                intent2.putExtra("UserType", userType);
                                                finish();
                                                startActivity(intent2);
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

    }
}
