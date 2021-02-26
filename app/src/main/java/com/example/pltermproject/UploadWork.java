package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class UploadWork extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    TextView assignmentName, assignmentDueDate, assignmentDescription, yourWorkBox;
    ImageButton uploadButton;
    String assignmentNameString, assignmentDueDateString, assignmentDescriptionString, assignmentID, assignmentIDfixed;
    String [] tokens2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_work);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        assignmentName = findViewById(R.id.assignment_name_fetched);
        assignmentDueDate = findViewById(R.id.due_date_fetched);
        assignmentDescription = findViewById(R.id.assignment_description_fetched);
        yourWorkBox = findViewById(R.id.your_work_edit_box);
        uploadButton = findViewById(R.id.upload_your_work_button);
        assignmentDescription.setFocusableInTouchMode(false);
        assignmentDescription.setFocusable(false);
        getSupportActionBar().setTitle("Upload Your Work");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            assignmentNameString = bundle.getString("assignmentName");
            assignmentDueDateString = bundle.getString("assignmentDueDate");
            assignmentDescriptionString = bundle.getString("assignmentDescription");
            assignmentID = bundle.getString("assignmentID");
        }
        assignmentName.setText(assignmentNameString);
        assignmentDueDate.setText(assignmentDueDateString);
        assignmentDescription.setText(assignmentDescriptionString);
        assignmentIDfixed = bundle.getString("assignmentID");
        tokens2 = assignmentIDfixed.split("-");
        assignmentIDfixed = tokens2[0].trim() + tokens2[1].substring(1).trim();
        final DocumentReference documentReference = fStore.collection("assignments").document(assignmentIDfixed);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, String> assignments = new HashMap<>();
                        assignments = (Map<String, String>) document.get("uploads");
                        if (assignments.containsKey(fAuth.getCurrentUser().getUid())){
                            yourWorkBox.setText(assignments.get(fAuth.getCurrentUser().getUid()));
                        }
                    }
                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference documentReference = fStore.collection("assignments").document(assignmentIDfixed);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){

                                Map<String, String> assignments = new HashMap<>();
                                assignments = (Map<String, String>) document.get("uploads");
                                assignments.put(fAuth.getCurrentUser().getUid(), yourWorkBox.getText().toString());
                                documentReference.update("uploads", assignments).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(UploadWork.this, "Work Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(UploadWork.this, "Error Uploading Work!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    }
                });
            }
        });
    }

    public boolean onSupportNavigateUp(){
            final Intent intent2 = new Intent(UploadWork.this,AssignmentsInfo.class);
            intent2.putExtra("UserType", "student");
            intent2.putExtra("assignmentID", assignmentID);
            finish();
            startActivity(intent2);
            return true;
    }

}
