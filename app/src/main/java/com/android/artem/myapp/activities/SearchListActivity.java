package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.artem.myapp.util.Act;
import com.android.artem.myapp.R;
import com.android.artem.myapp.model.Song;
import com.android.artem.myapp.adapter.SongAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class SearchListActivity extends Fragment {

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
    private int columntCount;

    private EditText searchEditText;
    private ImageButton searchImageButton;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search_list, container, false);

        columntCount = getResources().getInteger(R.integer.column_count);




        searchEditText = view.findViewById(R.id.searchEditText);
        changesTextSearchEditText();

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        //storage = FirebaseStorage.getInstance();


        //usersDatabaseReference = database.getReference().child("users");
        songsDatabaseReference = database.getReference().child("Songs");

        favouriteArrayList = new ArrayList<>();
        favouriteSongAdapter = new SongAdapter(getContext(), favouriteArrayList);

        songsArrayList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), songsArrayList);
        songRecyclerView = view.findViewById(R.id.recyclerView);
        songRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), columntCount));
        songRecyclerView.setAdapter(songAdapter);



        Song song = new Song();
        song.setTitle("Let It Be");
        song.setId("https://firebasestorage.googleapis.com/v0/b/myapp-72b61.appspot.com/o/song%2Fmaroon_5_-_this_love_pesni_na_den_svjatogo_valentina_(zf.fm).mp3?alt=media&token=3cd53128-0abb-4d13-a259-8e747fb4c3a5");
        song.setGroup("Maroon 5");
        song.setImage("https://firebasestorage.googleapis.com/v0/b/myapp-72b61.appspot.com/o/image%2Fmaroon5.jpg?alt=media&token=cb23f06c-371a-43ef-928c-f75fcd581bd0");
        //songsDatabaseReference.push().setValue(song);

       
        loadSongs();
        /*Thread thread = new Thread(){
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
        thread.start();*/

        return view;

    }



    @Override
    public void onResume() {
        super.onResume();
        Act.act=1;
    }

    private void searchSongs() {

        final String queryString = searchEditText.getText().toString().trim().toUpperCase();
        favouriteArrayList.clear();
        songsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("title").getValue().toString().toUpperCase().contains(queryString)) {

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
        songsDatabaseReference.removeEventListener(songsChildEventListener);
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
                    searchEditText.clearFocus();
                    songRecyclerView.setAdapter(songAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchEditText.setCursorVisible(false);
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
