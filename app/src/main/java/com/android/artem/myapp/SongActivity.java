package com.android.artem.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class SongActivity extends AppCompatActivity {


    private TextView titleTextView, groupTextView, elapsedTimeLabel, remainingTimeLabel;
    private ImageView imageView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String urlSong;
    private MediaPlayer mp;

    private SeekBar positionBar;
    private Button playButton;
    private int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        titleTextView = findViewById(R.id.titleTextView);
        groupTextView = findViewById(R.id.groupTextView);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);


        playButton = findViewById(R.id.playButton);
        positionBar = findViewById(R.id.positionBar);


        Intent intent = getIntent();
        urlSong = intent.getStringExtra("Id");
        titleTextView.setText(intent.getStringExtra("Title"));
        groupTextView.setText(intent.getStringExtra("Group"));

        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(0, 1);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                   createMediaPlayer();

                }
            }
        };
        thread.start();



    }

    public void createMediaPlayer(){
        mp = MediaPlayer.create(getApplicationContext(), Uri.parse(urlSong));
        mp.setLooping(true);
        mp.seekTo(0);
        mp.setVolume(0.5f, 0.5f);
        totalTime = mp.getDuration();

        // Position Bar
        positionBar = findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );



        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(10);
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
        mp.start();

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime-currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }






    @Override
    protected void onStop() {
        super.onStop();
        if (mp != null) {
            mp.stop();
        }
        finish();
    }


    public void playButton(View view) {
        if (!mp.isPlaying()) {
            // Stopping
            mp.start();
            playButton.setBackgroundResource(R.drawable.stop);

        } else {
            // Playing
            mp.pause();
            playButton.setBackgroundResource(R.drawable.play);
        }
    }
}
