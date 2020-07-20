package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.artem.myapp.util.Act;
import com.android.artem.myapp.R;
import com.android.artem.myapp.model.Song;
import com.android.artem.myapp.adapter.SongAdapter;
import com.android.artem.myapp.activities.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchListActivity extends Fragment{

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
    private int columnCount;

    private EditText searchEditText;
    private ImageButton searchImageButton;
    private TextView networkCheck;

    private Context context;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search_list, container, false);

        songRecyclerView = view.findViewById(R.id.recyclerView);
        columnCount = getResources().getInteger(R.integer.column_count);
        songRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), columnCount));

        context = getContext();



        searchEditText = view.findViewById(R.id.searchEditText);
        networkCheck = view.findViewById(R.id.networkCheck);
        searchEditText.setVisibility(View.GONE);

        favouriteArrayList = new ArrayList<>();
        favouriteSongAdapter = new SongAdapter(getContext(), favouriteArrayList);

        songsArrayList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), songsArrayList);


        songRecyclerView.setAdapter(songAdapter);

        if(isNetworkAvailable(getContext())){
            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            songsDatabaseReference = database.getReference().child("Songs");
            songRecyclerView.setVisibility(View.VISIBLE);
            networkCheck.setVisibility(View.GONE);
            changesTextSearchEditText();
            loadSongs();
        }else{
            songRecyclerView.setVisibility(View.GONE);
            networkCheck.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            networkCheck.setVisibility(View.VISIBLE);
        }



        //storage = FirebaseStorage.getInstance();


        //usersDatabaseReference = database.getReference().child("users");

        String title="";
        String id="";
        String group="";




        Song song = new Song();
        song.setTitle("");
        song.setId("");
        song.setGroup("");
        song.setImage("");
        //for(int i=0; i<10; i++)
        songsDatabaseReference.push().setValue(song);
        adminSongs();
       

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

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }





    @Override
    public void onResume() {
        super.onResume();
        Act.act=1;
        columnCount = getResources().getInteger(R.integer.column_count);
        songRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), columnCount));
    }




    private void searchSongs() {

        final String queryString = searchEditText.getText().toString().trim().toUpperCase();
        favouriteArrayList.clear();

        songsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("title").getValue().toString().toUpperCase().trim().contains(queryString)) {

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
        //songsDatabaseReference.removeEventListener(songsChildEventListener);
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


        songsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Song song = dataSnapshot1.getValue(Song.class);
                    songsArrayList.add(song);




                }
                Collections.shuffle(songsArrayList);
                songAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });

        /*songsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot!=null) {
                    Song song = dataSnapshot.getValue(Song.class);
                    songsArrayList.add(song);

                    Collections.shuffle(songsArrayList);
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
        songsDatabaseReference.addChildEventListener(songsChildEventListener);*/

    }






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);


        super.onCreate(savedInstanceState);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            MenuItem item = menu.getItem(1);
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
            Intent intent = new Intent(getContext(), SignInActivity.class);
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                intent.putExtra("signOut", true);
            }

            startActivity(intent);
            getActivity().finish();
        }


        return super.onOptionsItemSelected(item);
    }



    private void adminSongs(){

        songsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("id").getValue().toString().toUpperCase().trim().equals("")) {

                        dataSnapshot.getRef().removeValue();


                    }



                    //songsDatabaseReference.removeEventListener(songsChildEventListener);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
