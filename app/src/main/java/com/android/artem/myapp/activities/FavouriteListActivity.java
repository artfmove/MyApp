package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
