package com.android.artem.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private DatabaseReference songsDatabaseReference;
    private DatabaseReference usersDatabaseReference;

    private ChildEventListener songsChildEventListener;
    private FirebaseAuth auth;


    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private List<Song> songsArrayList;
    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private RecyclerView.LayoutManager songLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        //auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        //storage = FirebaseStorage.getInstance();

        //usersDatabaseReference = database.getReference().child("users");
        songsDatabaseReference = database.getReference().child("Songs");


        songsArrayList = new ArrayList<>();
        songRecyclerView = findViewById(R.id.recyclerView);
        songAdapter = new SongAdapter(this, songsArrayList);
        songRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        songRecyclerView.setAdapter(songAdapter);


        /*Song song = new Song();
        song.setTitle("Все в зале");
        song.setId("https://firebasestorage.googleapis.com/v0/b/myapp-72b61.appspot.com/o/song%2FDiskoteka%2BAvariya%2BVse%2Bv%2Bzale%2Bdvigaytes%2Bs%2Bnami.mp3?alt=media&token=1549f035-79a2-48d9-86aa-56530f3fa9e1");
        song.setGroup("Руки Вверх");
        songsDatabaseReference.push().setValue(song);*/


        songsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Song song = dataSnapshot.getValue(Song.class);
                //for(int i=0; i<dataSnapshot.getChildrenCount(); i++){
                    songsArrayList.add(song);
                    songAdapter.notifyDataSetChanged();
                //}


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        songsDatabaseReference.addChildEventListener(songsChildEventListener);



    }












    /*public void download(){
        songStorageReference.child("song1.mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(FavouriteActivity.this, uri+"", Toast.LENGTH_SHORT).show();
                MediaPlayer mp;
                mp = MediaPlayer.create(getApplicationContext(), uri);
                mp.start();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(FavouriteActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

}
