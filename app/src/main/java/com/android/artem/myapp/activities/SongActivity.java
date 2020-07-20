package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.PlaybackParams;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.artem.myapp.data.CacheAppData;
import com.android.artem.myapp.isNetwork;
import com.android.artem.myapp.model.Cache;
import com.android.artem.myapp.util.Act;
import com.android.artem.myapp.R;
import com.android.artem.myapp.model.Song;
import com.android.artem.myapp.util.mColor;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.android.artem.myapp.isNetwork;
import com.android.artem.myapp.util.Act;
import com.android.artem.myapp.activities.MainActivity;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Random;


public class SongActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView titleTextView, groupTextView, speedTextView;
    private ImageView previewImageView;
    private SeekBar speedSeekbar;
    private ImageButton downloadButton, moreButton;


    private DatabaseReference songsDatabaseReference;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private StorageReference storageRef;
    private StorageReference islandRef;
    private StorageReference httpsReference;
    private ValueEventListener listener;
    private ValueEventListener deleteListener;


    private String urlImage, title, group;
    public static String urlSong;
    private String urlCacheImage;

    private boolean isAdded = true;


    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlayerView playerView;
    private SimpleExoPlayer player;

    private Intent intent;

    private ProgressBar progressBar;


    private PlaybackParams param;
    private CacheAppData cacheAppData;
    private AlertDialog alertDialog;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_song);



        cacheAppData = Room.databaseBuilder(getApplicationContext(), CacheAppData.class, "AllCacheDB")
                .allowMainThreadQueries()
                .build();


        initialization();

        getIntentExtra();

        if(isNetwork.isLogIn()){
            ifNetworkAvailable();
        }





        speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                progress++;
                if (progress == 6) {
                    progress = 10;
                    speedTextView.setText("1.0");
                } else {
                    progress += 4;
                    speedTextView.setText("0." + progress);
                }

                speedTextView.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2 - 20);

                PlaybackParameters param = new PlaybackParameters(progress / 10f);
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





        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetwork.isLogIn()) {
                    showIsLogIn("For a get more features please Sign In");
                    return;
                }

                if (isCached()) {
                    deleteSong(true);
                } else {
                    try {
                        downloadSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });





        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetwork.isLogIn()) {
                    showIsLogIn("For a get more features please Sign In");
                    return;
                }
                showMore();
            }
        });


    }


    public void initialization(){
        speedSeekbar = findViewById(R.id.speedSeekBar);
        speedTextView = findViewById(R.id.speedTextView);
        titleTextView = findViewById(R.id.titleTextView);
        groupTextView = findViewById(R.id.groupTextView);
        previewImageView = findViewById(R.id.previewImageView);

        downloadButton = findViewById(R.id.downloadButton);
        moreButton = findViewById(R.id.moreButton);
        progressBar = findViewById(R.id.progressBar);
        playerView = findViewById(R.id.video_view);

        titleTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate));

        param = new PlaybackParams();

        if (isCached()) {
            downloadButton.setImageResource(R.drawable.ic_cloud_download_red_24dp);
        }





    }


    public void showIsLogIn(String text){
        builder = new AlertDialog.Builder(this);
        auth = FirebaseAuth.getInstance();


        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        builder.setMessage(text).setPositiveButton("Go to Log/Sign In, ", dialogClickListener)
                .setNegativeButton("No, maybe later", dialogClickListener)
                .setTitle("Choose")
                .setIcon(R.drawable.ic_done_white_24dp)
                .show();

    }


    private void ifNetworkAvailable(){
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            httpsReference = storage.getReferenceFromUrl(urlSong);
            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            songsDatabaseReference = database.getReference().child("SongsFav").child(user.getUid());

    }



    private void getIntentExtra(){
        intent = getIntent();
        title = intent.getStringExtra("title");
        titleTextView.setText(title);
        group = intent.getStringExtra("group");
        groupTextView.setText(group);
        urlImage = intent.getStringExtra("image");
        urlSong = intent.getStringExtra("id");

        int gColors = intent.getIntExtra("color", 0);

        GradientDrawable gd2 = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {gColors, Color.parseColor("#000000")});
        gd2.setCornerRadius(0f);
        LinearLayout background = findViewById(R.id.background);
        background.setBackground(gd2);

        /*Glide.with(this)
                .load(urlImage) // image url
                .placeholder(R.drawable.ic_music_note_black_24dp) // any placeholder to load at start
                .error(R.drawable.ic_music_note_black_24dp)  // any image in case of error
                // resizing
                .into(previewImageView);*/

    }

    private void showMore() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view = layoutInflaterAndroid.inflate(R.layout.show_more, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(view);


        alertDialogBuilderUserInput
                .setCancelable(true)
        ;


        alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
        CardView cacheCardView = alertDialog.findViewById(R.id.deleteFromCache);
        CardView favouritesCardView = alertDialog.findViewById(R.id.deleteFromFav);
        CardView addFavCardView = alertDialog.findViewById(R.id.addToFavourites);
        CardView addCacheCardView = alertDialog.findViewById(R.id.addToCache);
        if (Act.act == 2) {
            addFavCardView.setVisibility(View.GONE);
            if (isCached()) {
                addCacheCardView.setVisibility(View.GONE);
            }
        }

        cacheCardView.setOnClickListener(this);
        favouritesCardView.setOnClickListener(this);
        addFavCardView.setOnClickListener(this);
        addCacheCardView.setOnClickListener(this);


    }


    private boolean isCached() {
        boolean isTrue = false;
        if (cacheAppData.getCacheDAO().getDownloadCache(urlSong) != null && cacheAppData.getCacheDAO().getDownloadCache(urlSong).getNetUrl().equals(urlSong)) {
            isTrue = true;
        }
        return isTrue;
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri;



        if (isCached()) {

            uri = Uri.parse(cacheAppData.getCacheDAO().getDownloadCache(urlSong).getUrl());
        } else {
            uri = Uri.parse(urlSong);
            //uri = Uri.parse("https://files.freemusicarchive.org/storage-freemusicarchive-org/music/KEXP/Summer_Babes/KEXP_Live_Feb_2011/Summer_Babes_-_15_-_Home_Alone_II_Live__KEXP.mp3");

        }

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


    public void addToFavourites() {

        if (!isNetwork.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Check your network", Toast.LENGTH_SHORT).show();
            return;
        }
        listener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Song song = new Song();
                    song.setTitle(title);
                    song.setId(urlSong);
                    song.setGroup(group);
                    song.setImage(urlImage);
                    songsDatabaseReference.push().setValue(song);
                    Toast.makeText(SongActivity.this, title + " has been added", Toast.LENGTH_LONG).show();
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
                        Song song = new Song();
                        song.setTitle(title);
                        song.setId(urlSong);
                        song.setGroup(group);
                        song.setImage(urlImage);
                        songsDatabaseReference.push().setValue(song);
                        Toast.makeText(SongActivity.this, title + " has been added", Toast.LENGTH_LONG).show();
                        songsDatabaseReference.removeEventListener(listener);

                    }else{
                        Toast.makeText(SongActivity.this, "Song has been already added ", Toast.LENGTH_LONG).show();
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




    private void deleteSong(boolean fromCache) {
        if(!fromCache){
            if (!isNetwork.isNetworkAvailable(getApplicationContext())) {
                Toast.makeText(this, "Check your network", Toast.LENGTH_SHORT).show();
                return;
            }
            deleteListener = (new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.child("title").getValue().equals(title)) {
                            dataSnapshot1.getRef().removeValue();
                            Toast.makeText(SongActivity.this, "Song has been deleted", Toast.LENGTH_SHORT).show();
                            songsDatabaseReference.removeEventListener(deleteListener);

                            try {
                                Cache cache = cacheAppData.getCacheDAO().getDownloadCache(urlSong);
                                cacheAppData.getCacheDAO().deleteCache(cache);
                                File file = new File(cache.getUrl());
                                file.delete();
                                downloadButton.setImageResource(R.drawable.ic_cloud_download_white_24dp);
                            } catch (Exception e) {

                            }

                            finish();
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

        }else{
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Cache cache = cacheAppData.getCacheDAO().getDownloadCache(urlSong);
                            cacheAppData.getCacheDAO().deleteCache(cache);
                            Toast.makeText(getApplicationContext(), "Song has been deleted from cache.", Toast.LENGTH_SHORT).show();
                            File file = new File(cache.getUrl());
                            file.delete();
                            downloadButton.setImageResource(R.drawable.ic_cloud_download_white_24dp);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You want to delete this cache?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }



    }


    public void downloadSong() throws IOException {
        if (isCached()) {
            Toast.makeText(this, "Song has been cached", Toast.LENGTH_SHORT).show();
            return;
        } else if(progressBar.getVisibility()==View.VISIBLE) {
            return;

        }
        else {

            downloadButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);


            islandRef = storage.getReferenceFromUrl(urlSong);


            final File localFile = File.createTempFile("song", "mp3");


            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    cacheAppData.getCacheDAO().addCache(new Cache(0, localFile.toString(), urlSong, group, title, null));


                    releasePlayer();
                    initializePlayer();
                    downloadButton.setImageResource(R.drawable.ic_cloud_download_red_24dp);


                    progressBar.setVisibility(View.GONE);
                    downloadButton.setVisibility(View.VISIBLE);

                    addToFavourites();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(SongActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    downloadButton.setVisibility(View.VISIBLE);

                }
            });


        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addToFavourites:
                addToFavourites();
                alertDialog.dismiss();
                return;
            case R.id.addToCache:
                try {
                    downloadSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                alertDialog.dismiss();
                return;
            case R.id.deleteFromCache:
                deleteSong(true);
                alertDialog.dismiss();
                return;
            case R.id.deleteFromFav:
                deleteSong(false);
                alertDialog.dismiss();
                return;
        }

    }

}
