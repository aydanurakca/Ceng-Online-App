package com.example.pltermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

public class Stream extends AppCompatActivity implements Dialog.DialogListener{

    int[] IMAGES = {R.drawable.avatar_1, R.drawable.avatar_10, R.drawable.avatar_13, R.drawable.avatar_15, R.drawable.avatar_16, R.drawable.avatar_5, R.drawable.avatar_4, R.drawable.avatar_8};
    ListView listView;
    String courseId, userType, clickedItem;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView share;
    ConstraintLayout shareLayout;
    ImageButton courseInfo;
    ImageView cat;
    CustomAdapter customAdapter;
    ArrayList<DocumentSnapshot> postArray = new ArrayList();
    ArrayList<DocumentSnapshot> holderArray = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        listView = findViewById(R.id.streamListView);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        shareLayout = findViewById(R.id.share_layout);
        courseInfo = findViewById(R.id.course_info_button);
        share = findViewById(R.id.share_text);
        cat = findViewById(R.id.cat_or_bird);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            courseId = bundle.getString("CourseId");
            userType = bundle.getString("UserType");
        }
        if(userType.equals("student")){

            shareLayout.setBackground(ContextCompat.getDrawable(Stream.this, R.drawable.studentbackground));
            share.setVisibility(View.GONE);
            cat.setVisibility(View.GONE);
        }
        if(userType.equals("teacher")){

            shareLayout.setBackground(ContextCompat.getDrawable(Stream.this, R.drawable.sharebackground));

        }
        getSupportActionBar().setTitle(courseId + " Stream");

        final CollectionReference questionRef = fStore.collection("posts");
        questionRef.whereEqualTo("course", courseId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final QuerySnapshot qSnap = task.getResult();
                    if (!qSnap.isEmpty()) {
                        questionRef.orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    final QuerySnapshot querySnapshot = task.getResult();
                                    if (!querySnapshot.isEmpty()) {
                                        for (DocumentSnapshot doc: querySnapshot){
                                            for (DocumentSnapshot post: qSnap){
                                                if(doc.getId().equals(post.getId())){
                                                    holderArray.add(doc);
                                                }
                                            }
                                        }
                                        customAdapter = new CustomAdapter(holderArray.size(), courseId, holderArray);
                                        listView.setAdapter(customAdapter);
                                    }
                                }
                            }
                        });

                    }
                }
            }
        });

        if(userType.equals("teacher")){
            shareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String userName = document.get("name").toString();
                                    final Intent intent = new Intent(Stream.this, AddPostScreen.class);
                                    intent.putExtra("UserType", userType);
                                    intent.putExtra("CourseId", courseId);
                                    intent.putExtra("UserName", userName);
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        }
                    });

                }
            });
        }

        if(userType.equals("teacher")){
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    clickedItem = customAdapter.getPostArray().get(position).getId();
                    openDialog(customAdapter.getPostArray().get(position).getId());
                    return true;
                }
            });
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Intent intent = new Intent(Stream.this, CommentsScreen.class);
                intent.putExtra("UserType",userType);
                intent.putExtra("CourseId",courseId);
                ArrayList<Comment> temp = new ArrayList();
                temp.addAll((Collection<? extends Comment>) customAdapter.getPostArray().get(position).get("comments"));
                intent.putExtra("commentsArray", temp );
                intent.putExtra("post", customAdapter.getPostArray().get(position).get("post").toString());
                intent.putExtra("postId", customAdapter.getPostArray().get(position).getId());
                finish();
                startActivity(intent);

            }
        });
        courseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Stream.this, CourseInfo.class);
                intent.putExtra("UserType",userType);
                intent.putExtra("CourseId",courseId);
                finish();
                startActivity(intent);
            }
        });


    }
    class  CustomAdapter extends BaseAdapter{


        private final int postCount;
        private final String  courseId;
        private final ArrayList<DocumentSnapshot> postArray;

        public CustomAdapter(int postCount, String courseId, ArrayList<DocumentSnapshot> postArray) {
            this.postCount = postCount;
            this.courseId = courseId;
            this.postArray = postArray;
        }

        public ArrayList<DocumentSnapshot> getPostArray() {
            return postArray;
        }

        @Override
        public int getCount() {
            return postCount;
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

            if(view == null) view = View.inflate(Stream.this, R.layout.customlayout, null);

            final ImageView imageView = view.findViewById(R.id.poster_image);
            final TextView textView_name = view.findViewById(R.id.poster_name);
            final TextView textView_date = view.findViewById(R.id.post_date);
            final TextView textView_post = view.findViewById(R.id.post_text_box);
            final TextView textView_comments = view.findViewById(R.id.post_comments);


            textView_name.setText(postArray.get(i).get("postedBy").toString());
            textView_date.setText(postArray.get(i).get("date").toString());
            textView_post.setText(postArray.get(i).get("post").toString());
            imageView.setImageResource(IMAGES[Integer.parseInt(postArray.get(i).get("image").toString())]);
            ArrayList<Map<String, Object>> comments;
            comments = (ArrayList<Map<String, Object>>) postArray.get(i).get("comments");
            int size = comments.size();
            String commentString;
            if (size == 0) {
                commentString = "Add class comment";
            } else {
                commentString = size + " class comments";
            }
            textView_comments.setText(commentString);
            return view;
        }
    }
    public boolean onSupportNavigateUp(){
        if(userType.equals("student")){
            final Intent intent2 = new Intent(Stream.this,CoursesStudent.class);
            finish();
            startActivity(intent2);
        }
        else if(userType.equals("teacher")){
            final Intent intent2 = new Intent(Stream.this,CoursesTeacher.class);
            finish();
            startActivity(intent2);
        }
        return true;
    }
    public void openDialog(String postId){
        Dialog dialog = new Dialog(postId);
        dialog.show(getSupportFragmentManager(), "dialog");
    }
    @Override
    public void onYesClicked(String postId){

        postId = clickedItem;
        final DocumentReference docRef = fStore.collection("posts").document(postId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Stream.this, "Post Deleted", Toast.LENGTH_SHORT).show();
                                    final Intent intent = new Intent(Stream.this, Stream.class);
                                    intent.putExtra("UserType",userType);
                                    intent.putExtra("CourseId",courseId);
                                    finish();
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(Stream.this, "ERROR!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
