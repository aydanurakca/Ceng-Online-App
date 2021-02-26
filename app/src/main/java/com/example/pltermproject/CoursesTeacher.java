package com.example.pltermproject;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;

        import java.util.ArrayList;

public class CoursesTeacher extends AppCompatActivity {

    ListView listView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    ArrayAdapter arrayAdapter;
    String userType;
    Button addButton;
    ArrayList<String> lectures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_teacher);
        getSupportActionBar().setTitle("Your Courses");
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        addButton = findViewById(R.id.new_message_button);
        listView = findViewById(R.id.sent_messages_list);
        userId = fAuth.getCurrentUser().getUid();
        final Intent intent = new Intent(CoursesTeacher.this, Stream.class);
        DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        lectures = (ArrayList) document.get("lectures");
                        arrayAdapter = new ArrayAdapter(CoursesTeacher.this, android.R.layout.simple_list_item_1,lectures);
                        userType = document.get("UserType").toString();
                        intent.putExtra("UserType",userType);
                        listView.setAdapter(arrayAdapter);
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("CourseId",listView.getItemAtPosition(position).toString());
                finish();
                startActivity(intent);

            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent2 = new Intent(CoursesTeacher.this, CourseInfo.class);
                intent2.putExtra("CourseId","0000");
                intent2.putExtra("UserType", userType);
                finish();
                startActivity(intent2);

            }
        });

    }
    public boolean onSupportNavigateUp(){
        final Intent intent2 = new Intent(CoursesTeacher.this,Teacher.class);
        startActivity(intent2);
        finish();
        return true;
    }
}
