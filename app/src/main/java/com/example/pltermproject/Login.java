package com.example.pltermproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class Login extends AppCompatActivity {


    EditText userNameHintText, passwordHintText;
    Button loginButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameHintText = findViewById(R.id.username_hint_text);
        passwordHintText = findViewById(R.id.password_hint_text);
        loginButton = findViewById(R.id.login_button);

        getSupportActionBar().setTitle("Login CENGOnline");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        if(fAuth.getCurrentUser() != null){
            if(userType == "student"){
                startActivity(new Intent(getApplicationContext(), Student.class));
                finish();
            }
            else if(userType == "teacher"){
                startActivity(new Intent(getApplicationContext(), Teacher.class));
                finish();
            }
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = userNameHintText.getText().toString().trim();
                String password = passwordHintText.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    userNameHintText.setError("Username is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordHintText.setError("Password is required.");
                    return;
                }
                fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged In",Toast.LENGTH_SHORT).show();
                            userId = fAuth.getCurrentUser().getUid();
                            DocumentReference docRef = fStore.collection("users").document(userId);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            userType = document.get("UserType").toString();
                                            switch (userType){
                                                case "student":
                                                    startActivity(new Intent(getApplicationContext(), Student.class));
                                                    break;
                                                case "teacher":
                                                    startActivity(new Intent(getApplicationContext(), Teacher.class));
                                                    break;
                                            }
                                        }
                                    }
                                }
                            });

                        }
                        else{
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
