package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.PlaybackParams;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import com.google.firebase.storage.StorageReference;



import java.io.File;
import java.io.IOException;
import java.net.URI;


public class SongActivity extends AppCompatActivity {


    private TextView titleTextView, groupTextView, speedTextView;
    private ImageView previewImageView;
    private SeekBar speedSeekbar;



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
    private Menu menu;

    private PlaybackParams param;
    private CacheAppData cacheAppData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);


        cacheAppData = Room.databaseBuilder(getApplicationContext(), CacheAppData.class, "AllCacheDB")
                .allowMainThreadQueries()
                .build();


        Log.d("uriii", urlSong+"");
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
        group = intent.getStringExtra("group");
        groupTextView.setText(group);
        urlImage = intent.getStringExtra("image");
        urlSong = intent.getStringExtra("id");
        //Log.e("uri", urlSong + " " + urlCacheImage);

        param = new PlaybackParams();

        FavouriteListActivity fvl = new FavouriteListActivity();


        if(fvl.isNetworkAvailable(getApplicationContext())){
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
    }

    private boolean isCached(){
        boolean isTrue = false;
        if(cacheAppData.getCacheDAO().getDownloadCache(urlSong)!=null && cacheAppData.getCacheDAO().getDownloadCache(urlSong).getNetUrl().equals(urlSong)){
           isTrue = true;
        }
        return isTrue;
    }

    private void initializePlayer(String uri2) {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri = null;

       Cache currentCache = new Cache();


        if(isCached()){

            uri = Uri.parse(cacheAppData.getCacheDAO().getDownloadCache(urlSong).getUrl());
        }else{
            uri = Uri.parse(String.valueOf(R.raw.song));
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        if (Act.act == 1) {
            inflater.inflate(R.menu.menu_item, menu);
        } else if (Act.act == 2) {
            inflater.inflate(R.menu.menu_item_fav, menu);
        }

        if (isCached()==true){
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_cloud_download_black_24dp));
        }
        return true;
    }


    public void listener() {

        if(!isNetworkAvailable(getApplicationContext())){
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()) {
            case R.id.addSong:
                listener();
                return true;
            case R.id.deleteFromFav:
                deleteSong();
                return true;
            case R.id.download:
                try {
                    downloadSong();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.deleteFromCache:
                deleteFromCache();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void deleteFromCache(){
        Cache cache = cacheAppData.getCacheDAO().getDownloadCache(urlSong);
        cacheAppData.getCacheDAO().deleteCache(cache);
        menu.getItem(1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_cloud_download_white_24dp));


    }

    private void deleteSong() {
        if(!isNetworkAvailable(getApplicationContext())){
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



                        Cache cache = cacheAppData.getCacheDAO().getDownloadCache(urlSong);
                        cacheAppData.getCacheDAO().deleteCache(cache);
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

        if(isCached()){
            Toast.makeText(this, "Song is already cached", Toast.LENGTH_SHORT).show();
            return;
        }

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

                Cache cache =cacheAppData.getCacheDAO().getCache(id);


                Log.e("uri", ""+ cache.getId() + cache.getUrl() + " "+ cache.getNetUrl());
                releasePlayer();
                initializePlayer(cache.getUrl().toString());

                menu.getItem(1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_cloud_download_black_24dp));
                Toast.makeText(SongActivity.this, "Your song is downloading", Toast.LENGTH_LONG).show();


               listener();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(SongActivity.this, "uri", Toast.LENGTH_SHORT).show();
            }
        });












    }
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
