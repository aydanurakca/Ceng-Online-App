package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommentsScreen extends AppCompatActivity {

    int[] IMAGES = {R.drawable.avatar_1, R.drawable.avatar_10, R.drawable.avatar_13, R.drawable.avatar_15, R.drawable.avatar_16, R.drawable.avatar_5, R.drawable.avatar_4, R.drawable.avatar_8};
    ListView listView;
    String courseId, userType, post, postId;
    TextView postText, sendCommentText;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ArrayList<Map<String, Object>> commentsArray = new ArrayList();
    ArrayList<Map<String, Object>> commentsArraySorted = new ArrayList();
    final Map<String, Object> comment = new HashMap<>();
    ArrayList<Date> datesArray = new ArrayList();
    ImageButton sendCommentButton;
    CommentsScreen.CustomAdapter customAdapter;
    Switch editSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_screen);

        listView = findViewById(R.id.commentsListView);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        postText = findViewById(R.id.post_text_box);
        sendCommentButton = findViewById(R.id.send_comment_button);
        sendCommentText = findViewById(R.id.send_comment_text);
        editSwitch = findViewById(R.id.edit_switch);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            courseId = bundle.getString("CourseId");
            userType = bundle.getString("UserType");
            postId = bundle.getString("postId");
            post = bundle.getString("post");
            commentsArray = (ArrayList<Map<String, Object>>) bundle.get("commentsArray");
        }
        if(userType.equals("student")){

            editSwitch.setVisibility(View.GONE);
        }
        getSupportActionBar().setTitle("Comments");

        for (Map<String, Object> map: commentsArray) {
            datesArray.add((Date) map.get("date"));
        }
        Collections.sort(datesArray, new Comparator<Date>() {
            public int compare(Date d1, Date d2) {
                return d2.compareTo(d1);
            }
        });

        for (Date date: datesArray) {
            for (int i = 0; i < commentsArray.size(); i++){
                if(commentsArray.get(i).get("date").equals(date)){
                    commentsArraySorted.add(commentsArray.get(i));
                }
            }
        }

        editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){

                    postText.setFocusable(true);
                    postText.setFocusableInTouchMode(true);
                    Toast.makeText(CommentsScreen.this, "Editing Post is Enabled", Toast.LENGTH_SHORT).show();
                }
                else{
                    final DocumentReference docRef = fStore.collection("posts").document(postId);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Date date = Calendar.getInstance().getTime();
                                    docRef.update("post", postText.getText().toString());
                                    docRef.update("date", date);
                                }
                            }
                        }
                    });
                    postText.setFocusable(false);
                    postText.setFocusableInTouchMode(false);
                    Toast.makeText(CommentsScreen.this, "Editing Post is Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Collections.reverse(commentsArraySorted);
        customAdapter = new CommentsScreen.CustomAdapter(commentsArraySorted.size(), commentsArraySorted);
        listView.setAdapter(customAdapter);

        postText.setText(post);


        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });






    }
    class  CustomAdapter extends BaseAdapter {


        private final int commentsCount;
        private final ArrayList<Map<String, Object>> commentsArray;

        public CustomAdapter(int commentsCount, ArrayList<Map<String, Object>> commentsArray) {
            this.commentsCount = commentsCount;
            this.commentsArray = commentsArray;
        }



        @Override
        public int getCount() {
            return commentsCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            if(view == null) view = View.inflate(CommentsScreen.this, R.layout.customlayout, null);

            final ImageView imageView = view.findViewById(R.id.poster_image);
            final TextView textView_name = view.findViewById(R.id.poster_name);
            final TextView textView_date = view.findViewById(R.id.post_date);
            final TextView textView_post = view.findViewById(R.id.post_text_box);
            final TextView textView_comments = view.findViewById(R.id.post_comments);


            imageView.setImageResource(IMAGES[Integer.parseInt(commentsArray.get(i).get("image").toString())]);
            textView_name.setText(commentsArray.get(i).get("sender").toString());
            textView_date.setText(commentsArray.get(i).get("date").toString());
            textView_post.setText(commentsArray.get(i).get("comment").toString());
            textView_comments.setVisibility(View.GONE);
            return view;
        }
    }

    public void addComment(){

        final Map<String, Object> commentsMap = new HashMap<>();
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final String sender;
                Date date = Calendar.getInstance().getTime();
                Random rand = new Random();
                int random = rand.nextInt(IMAGES.length);
                String image = String.valueOf(random);
                String commentText = sendCommentText.getText().toString();
                if(TextUtils.isEmpty(commentText)){
                    sendCommentText.setError("Comment is required.");
                    return;
                }
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        sender = task.getResult().get("name").toString();

                        comment.put("sender", sender);
                        comment.put("date", date);
                        comment.put("image", image);
                        comment.put("comment", commentText);
                        final DocumentReference docRef = fStore.collection("posts").document(postId);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if(doc.exists()){
                                        ArrayList temp;
                                        temp = (ArrayList) doc.get("comments");
                                        temp.add(comment);
                                        commentsMap.put("comments", temp);
                                        docRef.update(commentsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CommentsScreen.this, "Comment Sent", Toast.LENGTH_SHORT).show();
                                                    final Intent intent2 = new Intent(CommentsScreen.this, CommentsScreen.class);
                                                    intent2.putExtra("UserType", userType);
                                                    intent2.putExtra("CourseId", courseId);
                                                    intent2.putExtra("postId", postId);
                                                    intent2.putExtra("post", post);
                                                    commentsArray.add(comment);
                                                    intent2.putExtra("commentsArray", commentsArray);
                                                    finish();
                                                    startActivity(intent2);
                                                    return;
                                                } else {
                                                    Toast.makeText(CommentsScreen.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                                }
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
    public boolean onSupportNavigateUp(){

            final Intent intent2 = new Intent(CommentsScreen.this, Stream.class);
            intent2.putExtra("CourseId", courseId);
            intent2.putExtra("UserType", userType);
            finish();
            startActivity(intent2);
            return true;
    }
}
