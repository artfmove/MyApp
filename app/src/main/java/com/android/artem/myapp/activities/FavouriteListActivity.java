package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.artem.myapp.util.Act;
import com.android.artem.myapp.R;
import com.android.artem.myapp.model.Song;
import com.android.artem.myapp.adapter.SongAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouriteListActivity extends Fragment {

    private DatabaseReference songsDatabaseReference;
    private FirebaseAuth auth;
    private ChildEventListener songsChildEventListener;

    private List<Song> songsArrayList;
    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private RecyclerView.LayoutManager songLayoutManager;
    private int columntCount;

    private EditText searchEditText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favourite_list, container, false);


        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        columntCount = getResources().getInteger(R.integer.column_count);

        songsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SongsFav").child(user.getUid());

        songsArrayList = new ArrayList<>();
        songRecyclerView = view.findViewById(R.id.recyclerView);
        songAdapter = new SongAdapter(getContext(), songsArrayList);
        songRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), columntCount));
        songRecyclerView.setAdapter(songAdapter);

        searchEditText = view.findViewById(R.id.searchEditText);
        changesTextSearchEditText();

        return view;

    }



    /*@Override
    protected void onPostResume() {
        super.onPostResume();
        Act.act=2;
    }*/

    @Override
    public void onResume() {
        super.onResume();
        loadSongs();
        Act.act=2;
    }

    private void searchSongs() {

        final String queryString = searchEditText.getText().toString().trim().toUpperCase();
        songsArrayList.clear();
        songsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("title").getValue().toString().toUpperCase().contains(queryString)) {

                        Song song = dataSnapshot1.getValue(Song.class);

                        songsArrayList.add(song);
                        songAdapter.notifyDataSetChanged();
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
                    loadSongs();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchEditText.setCursorVisible(false);
            }
        });



        searchEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
    }



    public void loadSongs() {
        songsArrayList.clear();
        songAdapter.notifyDataSetChanged();
        songsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot != null) {
                    Song song = dataSnapshot.getValue(Song.class);
                    songsArrayList.add(song);
                    songAdapter.notifyDataSetChanged();
                } else {
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
