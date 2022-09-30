package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oncesen2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class ViewStoryActivity extends AppCompatActivity{
    private StoriesProgressView storiesProgressView;
    static List<String> imageList,imageKeyList,myStoryUserNameList,myStoryTimeList,myStorySeenList;
    static String time, seen,userId;
    int counter = 0;
    int yDown,moveY;
    CircleImageView viewStoryProfilePictureCircleImageView, viewStorySeenCircleImageView;
    TextView viewStoryUserName, viewStoryTime, viewStorySeenCountTextView;
    ImageView viewStoryImageView;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    View reverseStory,skipStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_storie);
        tanimla();

    }

    public static void getImageInfo(List<String> imageList1,List<String>imageKeyList1,List<String>myStoryUserNameList1,List<String>myStoryTimeList1,List<String>myStorySeenList1) {
        imageList = imageList1;
        imageKeyList=imageKeyList1;
        myStoryUserNameList = myStoryUserNameList1;
        myStoryTimeList = myStoryTimeList1;
        myStorySeenList = myStorySeenList1;
    }

    public void tanimla() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storiesProgressView = (StoriesProgressView) findViewById(R.id.storiesProgressView);
        viewStoryProfilePictureCircleImageView = (CircleImageView) findViewById(R.id.viewStoryProfilePictureCircleImageView);
        viewStorySeenCircleImageView = (CircleImageView) findViewById(R.id.viewStorySeenCircleImageView);
        viewStorySeenCountTextView = (TextView) findViewById(R.id.viewStorySeenCountTextView);
        viewStoryTime = (TextView) findViewById(R.id.viewStoryTime);
        viewStoryUserName = (TextView) findViewById(R.id.viewStoryUserName);
        viewStoryImageView = (ImageView) findViewById(R.id.viewStoryImageView);
        reverseStory= (View) findViewById(R.id.reverseStory);
        skipStory= (View) findViewById(R.id.skipStory);
        storiesProgressView.setStoriesCount(imageList.size()); // <- set stories
        storiesProgressView.setStoryDuration(3000L); // <- set a story duration
        Picasso.get().load(imageList.get(counter)).into(viewStoryImageView);
        viewStoryUserName.setText(myStoryUserNameList.get(counter));
        viewStoryTime.setText(myStoryTimeList.get(counter));
        viewStorySeenCountTextView.setText(myStorySeenList.get(counter));
        storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
            @Override
            public void onNext() {
                if (counter < imageList.size()) {
                    counter++;
                    Picasso.get().load(imageList.get(counter)).into(viewStoryImageView);
                    viewStoryUserName.setText(myStoryUserNameList.get(counter));
                    viewStorySeenCountTextView.setText(myStorySeenList.get(counter));
                    viewStoryTime.setText(myStoryTimeList.get(counter));

                }
            }

            @Override
            public void onPrev() {
                if (counter > 0) {
                    counter--;
                    Picasso.get().load(imageList.get(counter)).into(viewStoryImageView);
                    viewStoryUserName.setText(myStoryUserNameList.get(counter));
                    viewStorySeenCountTextView.setText(myStorySeenList.get(counter));
                    viewStoryTime.setText(myStoryTimeList.get(counter));

                }
            }

            @Override
            public void onComplete() {
                Intent intent = new Intent(ViewStoryActivity.this, EditStoryActivity.class);
                startActivity(intent);

            }

        });// <- set listener
        skipStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        reverseStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });

        viewStoryImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        yDown=(int)event.getY();
                        //yDown=event.getY();
                        storiesProgressView.pause();
                        return true;
                    case MotionEvent.ACTION_UP:
                        storiesProgressView.resume();
                        moveY=(int)event.getY();
                        //moveY=(int)event.getY();
                        ;
                        if(moveY-yDown<0){
                            storiesProgressView.pause();
                            Intent intent = new Intent(ViewStoryActivity.this, EditStoryActivity.class);
                            startActivity(intent);
                        }
                        return true;

                }
                return false;
            }
        });
        storiesProgressView.startStories(); // <- start progress


        databaseReference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("resim").getValue().toString()).into(viewStoryProfilePictureCircleImageView);
                //viewStoryUserName.setText(snapshot.child("kullaniciIsim").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }
}