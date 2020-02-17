package com.android.artem.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class SearchListActivity extends AppCompatActivity {

    private DatabaseReference songsDatabaseReference;
    private DatabaseReference usersDatabaseReference;

    private ChildEventListener songsChildEventListener;
    private FirebaseAuth auth;


    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private List<Song> songsArrayList;
    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;

    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        //storage = FirebaseStorage.getInstance();

        //usersDatabaseReference = database.getReference().child("users");
        songsDatabaseReference = database.getReference().child("Songs");


        songsArrayList = new ArrayList<>();
        songRecyclerView = findViewById(R.id.recyclerView);
        songAdapter = new SongAdapter(this, songsArrayList);
        songRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        songRecyclerView.setAdapter(songAdapter);

        Song song = new Song();
        song.setTitle("Lucky");
        song.setId("LuckyId");
        song.setGroup(" 123123213213Вверх");
        //songsDatabaseReference.push().setValue(song);
        loadSongs();
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(5000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    songsDatabaseReference.removeEventListener(songsChildEventListener);
                }
            }
        };
        thread.start();



    }

    public void loadSongs(){
        songsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot!=null) {
                    Song song = dataSnapshot.getValue(Song.class);
                    songsArrayList.add(song);
                    songAdapter.notifyDataSetChanged();
                }else{
                    songsDatabaseReference.removeEventListener(songsChildEventListener);
                }



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







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favouriteListSong:
                startActivity(new Intent(SearchListActivity.this, FavouriteListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    /*public void download(){
        songStorageReference.child("song1.mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(SearchListActivity.this, uri+"", Toast.LENGTH_SHORT).show();
                MediaPlayer mp;
                mp = MediaPlayer.create(getApplicationContext(), uri);
                mp.start();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(SearchListActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

}
