package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.artem.myapp.adapter.CacheSongAdapter;
import com.android.artem.myapp.data.CacheAppData;
import com.android.artem.myapp.isNetwork;
import com.android.artem.myapp.model.Cache;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FavouriteListActivity extends Fragment {

    private DatabaseReference songsDatabaseReference;
    private FirebaseAuth auth;
    private ChildEventListener songsChildEventListener;

    private List<Song> songsArrayList;
    private List<Cache> cacheSongsArrayList, cacheSongsArrayList2;
    public RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private CacheSongAdapter cacheSongAdapter;
    private int columnCount;

    private EditText searchEditText;

    private String titleCache="y";
    private CacheAppData cacheAppData;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favourite_list, container, false);


        context = getContext();



        songRecyclerView = view.findViewById(R.id.recyclerView);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            return null;
        }



        if(isNetwork.isNetworkAvailable(getContext())){
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            songsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SongsFav").child(user.getUid());
            songsArrayList = new ArrayList<>();
            songAdapter = new SongAdapter(getContext(), songsArrayList);
            songRecyclerView.setAdapter(songAdapter);
        }else{
            cacheSongsArrayList = new ArrayList<>();
            cacheSongsArrayList2 = new ArrayList<>();
            cacheSongAdapter = new CacheSongAdapter(getContext(), cacheSongsArrayList);
            songRecyclerView.setAdapter(cacheSongAdapter);

            cacheAppData = Room.databaseBuilder(getContext(), CacheAppData.class, "AllCacheDB")
                    .allowMainThreadQueries()
                    .build();

            cacheSongsArrayList.addAll(cacheAppData.getCacheDAO().getAllCaches());
            cacheSongsArrayList2.addAll(cacheSongsArrayList);
            cacheSongAdapter.notifyDataSetChanged();
        }










        searchEditText = view.findViewById(R.id.searchEditText);
        loadSongs();
        changesTextSearchEditText();

        return view;

    }



    @Override
    public void onResume() {
        super.onResume();
        if(isNetwork.isNetworkAvailable(getContext()))
        //loadSongs();
        Act.act=2;
        columnCount = getResources().getInteger(R.integer.column_count);
        songRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), columnCount));
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
                if(isNetwork.isNetworkAvailable(getContext())){
                    if(s.toString().trim().length()>0){
                        searchSongs();
                    }else{
                        searchEditText.clearFocus();
                        loadSongs();
                    }
                }else{
                    if(s.toString().trim().length()>0){
                        searchCacheSongs();
                    }else{
                        searchEditText.clearFocus();
                        cacheSongsArrayList.clear();
                        cacheSongsArrayList.addAll(cacheAppData.getCacheDAO().getAllCaches());
                        cacheSongAdapter.notifyDataSetChanged();
                    }
                }



            }

            @Override
            public void afterTextChanged(Editable s) {
                searchEditText.setCursorVisible(false);
            }
        });



        searchEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
    }

    private void searchCacheSongs(){

        final String queryString = searchEditText.getText().toString().trim().toUpperCase();
        cacheSongAdapter.notifyDataSetChanged();


        cacheSongsArrayList.clear();
        Cache cache1;

        for(int i=0; i<cacheSongsArrayList2.size(); i++){

            cache1 = cacheSongsArrayList2.get(i);

            if (cache1.getTitle().toUpperCase().contains(queryString) && !titleCache.contains(cache1.getTitle().toUpperCase())){
                cacheSongsArrayList.add(cache1);

            }
            titleCache = cache1.getTitle().toUpperCase();
        }
        cacheSongAdapter.notifyDataSetChanged();

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            MenuItem item = menu.getItem(0);
            item.setVisible(false);
            item = menu.getItem(1);
            item.setTitle("Sign In");
        }

        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.search_bar){
            if(Act.isSearchOn){
                searchEditText.setVisibility(View.GONE);
                searchEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                Act.isSearchOn=false;
            }else {


                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.setFocusableInTouchMode(true);
                searchEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText, InputMethodManager.RESULT_UNCHANGED_HIDDEN);
                Act.isSearchOn=true;
            }
        }
        if(id==R.id.signOut){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), SignInActivity.class));
            getActivity().finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
