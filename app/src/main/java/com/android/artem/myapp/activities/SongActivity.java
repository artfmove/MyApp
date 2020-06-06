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
import com.android.artem.myapp.model.Cache;
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


import java.io.File;
import java.io.IOException;
import java.net.URI;


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


        Log.d("uriii", urlSong + "");
        speedSeekbar = findViewById(R.id.speedSeekBar);
        speedTextView = findViewById(R.id.speedTextView);
        titleTextView = findViewById(R.id.titleTextView);
        groupTextView = findViewById(R.id.groupTextView);
        previewImageView = findViewById(R.id.previewImageView);

        downloadButton = findViewById(R.id.downloadButton);
        moreButton = findViewById(R.id.moreButton);
        progressBar = findViewById(R.id.progressBar);


        titleTextView.startAnimation((Animation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate));


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


        playerView = findViewById(R.id.video_view);

        intent = getIntent();
        title = intent.getStringExtra("title");
        titleTextView.setText(title);
        group = intent.getStringExtra("group");
        groupTextView.setText(group);
        urlImage = intent.getStringExtra("image");
        urlSong = intent.getStringExtra("id");
        //Log.e("uri", urlSong + " " + urlCacheImage);

        param = new PlaybackParams();

        FavouriteListActivity fvl = new FavouriteListActivity();


        if (fvl.isNetworkAvailable(getApplicationContext())) {
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            httpsReference = storage.getReferenceFromUrl(urlSong);
            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            songsDatabaseReference = database.getReference().child("SongsFav").child(user.getUid());
        }


        Glide.with(this)
                .load(urlImage) // image url
                .placeholder(R.drawable.ic_music_note_black_24dp) // any placeholder to load at start
                .error(R.drawable.ic_music_note_black_24dp)  // any image in case of error
                // resizing
                .into(previewImageView);


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCached()) {
                    deleteFromCache();
                } else {
                    try {
                        downloadSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        if (isCached()) {
            downloadButton.setImageResource(R.drawable.ic_cloud_download_red_24dp);
        }


        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore();
            }
        });
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

    private void initializePlayer(String uri2) {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = null;

        Cache currentCache = new Cache();


        if (isCached()) {

            uri = Uri.parse(cacheAppData.getCacheDAO().getDownloadCache(urlSong).getUrl());
        } else {
            //uri = Uri.parse(urlSong);
            //uri = Uri.parse("https://files.freemusicarchive.org/storage-freemusicarchive-org/music/KEXP/Summer_Babes/KEXP_Live_Feb_2011/Summer_Babes_-_15_-_Home_Alone_II_Live__KEXP.mp3");

        }

        //uri = Uri.parse(cacheAppData.getCacheDAO().getAllCaches().getUrl().toString());


        //Uri uri = Uri.parse("file:/data/user/0/com.android.artem.myapp/cache/song1664556465848270998mp3");

        MediaSource mediaSource = buildMediaSource(uri);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer(null);
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


    public void listener() {

        if (!isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "It's impossible to add offline", Toast.LENGTH_SHORT).show();
            return;
        }
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


    private void deleteFromCache() {


        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Cache cache = cacheAppData.getCacheDAO().getDownloadCache(urlSong);
                        cacheAppData.getCacheDAO().deleteCache(cache);
                        Toast.makeText(getApplicationContext(), "This song caches was deleted.", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("You want delete this cache?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }

    private void deleteSong() {
        if (!isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "It's impossible to delete offline", Toast.LENGTH_SHORT).show();
            return;
        }
        deleteListener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("title").getValue().equals(title)) {
                        dataSnapshot1.getRef().removeValue();
                        Toast.makeText(SongActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                        songsDatabaseReference.removeEventListener(deleteListener);

                        try {
                            Cache cache = cacheAppData.getCacheDAO().getDownloadCache(urlSong);
                            cacheAppData.getCacheDAO().deleteCache(cache);
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

    }


    public void downloadSong() throws IOException {
        /*storageRef.child("song/Vremya_i_Steklo_-_Pesnya_pro_litso.mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                urlSong = uri.toString();
                releasePlayer();
                initializePlayer();
                Toast.makeText(SongActivity.this, urlSong, Toast.LENGTH_SHORT).show();
                Log.d("uriii", urlSong+"");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(SongActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });*/

        if (isCached()) {
            Toast.makeText(this, "Song is already cached", Toast.LENGTH_SHORT).show();
            return;
        } else if(progressBar.getVisibility()==View.VISIBLE) {
            return;

        }
        else {

            downloadButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(100);


            islandRef = storage.getReferenceFromUrl(urlImage);


            final File localFileImage = File.createTempFile("image", "jpg");

            islandRef.getFile(localFileImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    urlCacheImage = localFileImage.toString();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });


            islandRef = storage.getReferenceFromUrl(urlSong);
            //islandRef = storageRef.child("song/song1.mp3");

            final File localFile = File.createTempFile("song", "mp3");


            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    long id = cacheAppData.getCacheDAO().addCache(new Cache(0, localFile.toString(), urlSong, group, title, urlCacheImage));

                    Cache cache = cacheAppData.getCacheDAO().getCache(id);


                    //Log.e("uri", "" + cache.getId() + cache.getUrl() + " " + cache.getNetUrl());
                    releasePlayer();
                    initializePlayer(cache.getUrl().toString());
                    Log.e("cache", cache.getUrl());
                    downloadButton.setImageResource(R.drawable.ic_cloud_download_red_24dp);
                    //Toast.makeText(SongActivity.this, "Your song is downloading", Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);
                    downloadButton.setVisibility(View.VISIBLE);

                    listener();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(SongActivity.this, "uri", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    downloadButton.setVisibility(View.VISIBLE);

                }
            });


        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addToFavourites:
                listener();
                alertDialog.dismiss();
                return;
            case R.id.addToCache:
                try {
                    downloadSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                alertDialog.dismiss();;
                return;
            case R.id.deleteFromCache:
                deleteFromCache();
                alertDialog.dismiss();
                return;
            case R.id.deleteFromFav:
                deleteSong();
                alertDialog.dismiss();
                return;
        }

    }

}
