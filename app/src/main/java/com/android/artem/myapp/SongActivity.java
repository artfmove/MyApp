package com.android.artem.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class SongActivity extends AppCompatActivity {

    private TextView idTextView;
    private TextView titleTextView;
    private TextView groupTextView;
    private ImageView imageView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String urlSong;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);


        Intent intent = getIntent();
        urlSong = intent.getStringExtra("Id");
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(0,1);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    createMediaPlayer();
                }
            }
        };
        thread.start();





    }



    private void createMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(urlSong));
        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
        finish();
    }
}
