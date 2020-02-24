package com.android.artem.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SongActivity extends AppCompatActivity {


    private TextView titleTextView, groupTextView;
    private ImageView imageView;


    private DatabaseReference songsDatabaseReference;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ValueEventListener listener;


    private String urlSong;
    private String title;
    private String group;
    private boolean isAdded = true;


    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlayerView playerView;
    private SimpleExoPlayer player;

    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);



        /*titleTextView = findViewById(R.id.titleTextView);
        groupTextView = findViewById(R.id.groupTextView);
        imageView = findViewById(R.id.imageView);*/

        playerView = findViewById(R.id.video_view);

        intent = getIntent();
        urlSong = intent.getStringExtra("Id");
        title = intent.getStringExtra("Title");




        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        songsDatabaseReference = database.getReference().child("SongsFav").child(user.getUid());


        /*titleTextView.setText(intent.getStringExtra("Title"));
        groupTextView.setText(intent.getStringExtra("Group"));*/
        /*Picasso.get().load(intent.getStringExtra("Image")).fit().centerInside()
                .into(imageView);*/




    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = Uri.parse(urlSong);
        MediaSource mediaSource = buildMediaSource(uri);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                /*| View.SYSTEM_UI_FLAG_FULLSCREEN*/
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if(Act.act==1){
            inflater.inflate(R.menu.menu_item, menu);
        }else if(Act.act==2){
            inflater.inflate(R.menu.menu_item_fav, menu);
        }
        return true;
    }


    public void listener() {
        listener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("title").getValue().equals(title)) {
                        isAdded = true;
                        break;
                    } else {
                        isAdded = false;

                    }

                }


                if (isAdded == false) {
                    songsDatabaseReference.removeEventListener(listener);
                    Song song = new Song();
                    song.setTitle(intent.getStringExtra("Title"));
                    song.setId(urlSong);
                    song.setGroup(intent.getStringExtra("Group"));
                    song.setImage(intent.getStringExtra("Image"));
                    songsDatabaseReference.push().setValue(song);
                    Toast.makeText(SongActivity.this, "Song - " + title + " is added", Toast.LENGTH_LONG).show();
                    songsDatabaseReference.removeEventListener(listener);
                } else {
                    Toast.makeText(SongActivity.this, "Song is Already Added", Toast.LENGTH_LONG).show();
                    songsDatabaseReference.removeEventListener(listener);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        songsDatabaseReference.addValueEventListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addSong:
                listener();
                return true;
            case R.id.deleteSong:
                deleteSong();

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void deleteSong() {
        

    }


}
