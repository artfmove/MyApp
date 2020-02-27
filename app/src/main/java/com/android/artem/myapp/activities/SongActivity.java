package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.artem.myapp.util.Act;
import com.android.artem.myapp.R;
import com.android.artem.myapp.model.Song;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
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


public class SongActivity extends AppCompatActivity {


    private TextView titleTextView, groupTextView, speedTextView;
    private ImageView previewImageView;
    private SeekBar speedSeekbar;


    private DatabaseReference songsDatabaseReference;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ValueEventListener listener;
    private ValueEventListener deleteListener;


    private String urlSong, urlImage, title, group;

    private boolean isAdded = true;


    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlayerView playerView;
    private SimpleExoPlayer player;

    private Intent intent;


    private PlaybackParams param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        speedSeekbar = findViewById(R.id.speedSeekBar);
        speedTextView = findViewById(R.id.speedTextView);
        titleTextView = findViewById(R.id.titleTextView);
        groupTextView = findViewById(R.id.groupTextView);
        previewImageView = findViewById(R.id.previewImageView);


        speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                progress++;
                if(progress==6) {
                    progress=10;
                    speedTextView.setText("1.0");
                }
                else {
                    progress+=4;
                    speedTextView.setText("0."+ progress);
                }

                speedTextView.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2-20);

                PlaybackParameters param = new PlaybackParameters(progress/10f);
                player.setPlaybackParameters(param);
                speedTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                speedTextView.setVisibility(View.INVISIBLE);
            }

        });



        playerView = findViewById(R.id.video_view);

        intent = getIntent();
        title = intent.getStringExtra("title");
        titleTextView.setText(title);
        groupTextView.setText(intent.getStringExtra("group"));
        urlImage = intent.getStringExtra("image");
        urlSong = intent.getStringExtra("id");



        Glide.with(this)
                .load(urlImage) // image url
                .placeholder(R.drawable.ic_music_note_black_24dp) // any placeholder to load at start
                .error(R.drawable.ic_music_note_black_24dp)  // any image in case of error
                 // resizing
        .into(previewImageView);

        param = new PlaybackParams();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        songsDatabaseReference = database.getReference().child("SongsFav").child(user.getUid());
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = Uri.parse(urlSong);
        //Uri uri = Uri.parse("https://files.freemusicarchive.org/storage-freemusicarchive-org/music/KEXP/Summer_Babes/KEXP_Live_Feb_2011/Summer_Babes_-_15_-_Home_Alone_II_Live__KEXP.mp3");

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
                //| View.SYSTEM_UI_FLAG_FULLSCREEN
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
        if (Act.act == 1) {
            inflater.inflate(R.menu.menu_item, menu);
        } else if (Act.act == 2) {
            inflater.inflate(R.menu.menu_item_fav, menu);
        }
        return true;
    }


    public void listener() {
        listener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Song song = new Song();
                    song.setTitle(intent.getStringExtra("title"));
                    song.setId(urlSong);
                    song.setGroup(intent.getStringExtra("group"));
                    song.setImage(intent.getStringExtra("image"));
                    songsDatabaseReference.push().setValue(song);
                    Toast.makeText(SongActivity.this, "Song - " + title + " is added", Toast.LENGTH_LONG).show();
                    songsDatabaseReference.removeEventListener(listener);
                } else {


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
                        song.setTitle(intent.getStringExtra("title"));
                        song.setId(urlSong);
                        song.setGroup(intent.getStringExtra("group"));
                        song.setImage(intent.getStringExtra("image"));
                        songsDatabaseReference.push().setValue(song);
                        Toast.makeText(SongActivity.this, "Song - " + title + " is added", Toast.LENGTH_LONG).show();
                        songsDatabaseReference.removeEventListener(listener);
                    } else {
                        Toast.makeText(SongActivity.this, "Song is Already Added", Toast.LENGTH_LONG).show();
                        songsDatabaseReference.removeEventListener(listener);
                    }


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
        deleteListener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("title").getValue().equals(title)) {
                        dataSnapshot1.getRef().removeValue();
                        Toast.makeText(SongActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                        songsDatabaseReference.removeEventListener(deleteListener);
                        break;
                    } else {

                        songsDatabaseReference.removeEventListener(deleteListener);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        songsDatabaseReference.addValueEventListener(deleteListener);

    }


}
