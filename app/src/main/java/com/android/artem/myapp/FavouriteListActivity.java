package com.android.artem.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavouriteListActivity extends AppCompatActivity {

    private DatabaseReference songsDatabaseReference;
    private FirebaseAuth auth;
    private ChildEventListener songsChildEventListener;

    private List<Song> songsArrayList;
    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private RecyclerView.LayoutManager songLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        songsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SongsFav").child(user.getUid());

        songsArrayList = new ArrayList<>();
        songRecyclerView = findViewById(R.id.recyclerView);
        songAdapter = new SongAdapter(this, songsArrayList);
        songRecyclerView.setLayoutManager(new GridLayoutManager(FavouriteListActivity.this, 1));
        songRecyclerView.setAdapter(songAdapter);
        loadSongs();

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
}
