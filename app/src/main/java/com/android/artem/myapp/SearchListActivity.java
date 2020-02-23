package com.android.artem.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    private List<Song> favouriteArrayList;
    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private SongAdapter favouriteSongAdapter;

    private String userKey;

    private EditText searchEditText;
    private ImageButton searchImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        searchEditText = findViewById(R.id.searchEditText);
        changesTextSearchEditText();

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        //storage = FirebaseStorage.getInstance();

        //usersDatabaseReference = database.getReference().child("users");
        songsDatabaseReference = database.getReference().child("Songs");

        favouriteArrayList = new ArrayList<>();
        favouriteSongAdapter = new SongAdapter(this, favouriteArrayList);

        songsArrayList = new ArrayList<>();
        songAdapter = new SongAdapter(this, songsArrayList);
        songRecyclerView = findViewById(R.id.recyclerView);
        songRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        songRecyclerView.setAdapter(songAdapter);



        Song song = new Song();
        song.setTitle("Let It Be");
        song.setId("https://firebasestorage.googleapis.com/v0/b/myapp-72b61.appspot.com/o/song%2Fmaroon_5_-_this_love_pesni_na_den_svjatogo_valentina_(zf.fm).mp3?alt=media&token=3cd53128-0abb-4d13-a259-8e747fb4c3a5");
        song.setGroup("Maroon 5");
        song.setImage("https://firebasestorage.googleapis.com/v0/b/myapp-72b61.appspot.com/o/image%2Fmaroon5.jpg?alt=media&token=cb23f06c-371a-43ef-928c-f75fcd581bd0");
        //songsDatabaseReference.push().setValue(song);



        loadSongs();
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(10000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    songsDatabaseReference.removeEventListener(songsChildEventListener);
                }
            }
        };
        thread.start();



    }

    private void searchSongs() {

        final String queryString = searchEditText.getText().toString().trim();
        favouriteArrayList.clear();
        songsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("title").getValue().equals(queryString)) {

                        Song song = dataSnapshot1.getValue(Song.class);


                        favouriteArrayList.add(song);
                        songRecyclerView.setAdapter(favouriteSongAdapter);
                        favouriteSongAdapter.notifyDataSetChanged();
                    }



                        //songsDatabaseReference.removeEventListener(songsChildEventListener);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changesTextSearchEditText() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()>0){

                    searchSongs();
                }else{

                    songRecyclerView.setAdapter(songAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        searchEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
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
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SearchListActivity.this, SignInActivity.class));
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
